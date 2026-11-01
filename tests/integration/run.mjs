import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import net from 'node:net';
import { createRequire } from 'node:module';
import { execFileSync, spawn } from 'node:child_process';
import { remote } from 'webdriverio';
import { directory, root, appiumHome, appiumCli, drivers } from './paths.mjs';
import { Device, pause } from './device.mjs';
import { cases } from './suite.mjs';

const platform = process.argv[2];
assert(drivers[platform], 'Usage: npm run test:integration -- android|ios');
assert(platform !== 'ios' || process.platform === 'darwin', 'iOS tests require macOS and Xcode');
const require = createRequire(path.join(root, 'package.json'));
const cordova = require.resolve('cordova/bin/cordova');
const appId = 'com.yandex.ads.integration';
const stamp = new Date().toISOString().replace(/[:.]/g, '-');
const output = path.join(root, '.artifacts/integration', platform, stamp);
const work = path.join(root, '.cache/integration/apps', `${platform}-${stamp}`);
const app = path.join(work, 'app');
fs.mkdirSync(output, { recursive: true });
fs.mkdirSync(work, { recursive: true });
const buildLog = fs.openSync(path.join(output, 'build.log'), 'w');
const appiumLog = fs.openSync(path.join(output, 'appium.log'), 'w');
const nativeLog = fs.openSync(path.join(output, 'native.log'), 'w');
const results = [];
let server;
let nativeLogger;
let driver;
let simulatorStarted;
let fatal;

function command(binary, args, cwd = root) {
    execFileSync(binary, args, { cwd, stdio: ['ignore', buildLog, buildLog], timeout: 600000 });
}

function cli(args, cwd = app) { command(process.execPath, [cordova, ...args], cwd); }
function json(binary, args) {
    return JSON.parse(execFileSync(binary, args, {
        encoding: 'utf8', timeout: 60000, env: { ...process.env, APPIUM_HOME: appiumHome }
    }));
}

async function freePort() {
    const socket = net.createServer();
    await new Promise((resolve, reject) => socket.once('error', reject).listen(0, '127.0.0.1', resolve));
    const port = socket.address().port;
    await new Promise(resolve => socket.close(resolve));
    return port;
}

function deviceId() {
    if (platform === 'android') {
        const connected = execFileSync('adb', ['devices'], { encoding: 'utf8' }).split('\n').slice(1)
            .map(line => line.trim().split(/\s+/)).filter(([, state]) => state === 'device').map(([id]) => id);
        const selected = process.env.INTEGRATION_UDID || (connected.length === 1 ? connected[0] : null);
        assert(selected && connected.includes(selected), 'Boot an Android emulator; set INTEGRATION_UDID if multiple devices are connected');
        return selected;
    }
    const all = json('xcrun', ['simctl', 'list', 'devices', 'available', '--json']).devices;
    const devices = Object.entries(all).sort(([a], [b]) => b.localeCompare(a, undefined, { numeric: true }))
        .flatMap(([runtime, items]) => runtime.includes('iOS') ? items.filter(item => item.name.startsWith('iPhone')) : []);
    const device = process.env.INTEGRATION_UDID
        ? devices.find(item => item.udid === process.env.INTEGRATION_UDID)
        : devices.find(item => item.state === 'Booted') || devices[0];
    assert(device, 'Install an iOS Simulator runtime in Xcode; INTEGRATION_UDID may select a specific iPhone simulator');
    if (device.state !== 'Booted') {
        command('xcrun', ['simctl', 'boot', device.udid]);
        simulatorStarted = device.udid;
        command('xcrun', ['simctl', 'bootstatus', device.udid, '-b']);
    }
    return device.udid;
}

function xml(value) {
    return String(value).replace(/[<>&"']/g, character => ({ '<': '&lt;', '>': '&gt;', '&': '&amp;', '"': '&quot;', "'": '&apos;' }[character]));
}

function report() {
    const failures = results.filter(item => item.status === 'failed').length + (fatal ? 1 : 0);
    const rows = [...results, ...(fatal ? [{ name: 'infrastructure', status: 'failed', error: fatal.stack || String(fatal), duration: 0 }] : [])];
    fs.writeFileSync(path.join(output, 'results.json'), JSON.stringify({ platform, appId, failures, results: rows }, null, 2));
    fs.writeFileSync(path.join(output, 'junit.xml'), `<?xml version="1.0" encoding="UTF-8"?>\n<testsuite name="yandex-ads-${platform}" tests="${rows.length}" failures="${failures}">\n${rows.map(row => `  <testcase classname="${platform}" name="${xml(row.name)}" time="${row.duration / 1000}">${row.status === 'failed' ? `<failure message="${xml(row.error)}">${xml(row.error)}</failure>` : ''}</testcase>`).join('\n')}\n</testsuite>\n`);
    console.log(`Integration: ${results.length - results.filter(item => item.status === 'failed').length} passed, ${failures} failed. Reports: ${output}`);
    if (failures) process.exitCode = 1;
}

try {
    const selected = cases(platform).filter(item => !process.env.INTEGRATION_CASE || new RegExp(process.env.INTEGRATION_CASE).test(item.name));
    assert(selected.length, 'INTEGRATION_CASE did not select any cases');
    const installed = json(process.execPath, [appiumCli, 'driver', 'list', '--installed', '--json']);
    assert(installed[drivers[platform].name], `Run npm run test:integration:setup -- ${platform} first (APPIUM_HOME=${appiumHome})`);
    const udid = deviceId();
    nativeLogger = platform === 'android'
        ? spawn('adb', ['-s', udid, 'logcat', '-v', 'threadtime', '-T', '1', 'YandexAdsPlugin:D', 'YandexAds:D', 'Ads:D', 'AndroidRuntime:E', 'chromium:I', '*:S'], { stdio: ['ignore', nativeLog, nativeLog] })
        : spawn('xcrun', ['simctl', 'spawn', udid, 'log', 'stream', '--style', 'compact', '--level', 'debug', '--predicate', 'process == "YandexAdsIntegration"'], { stdio: ['ignore', nativeLog, nativeLog] });
    let artifact;
    if (process.env.INTEGRATION_APP) {
        artifact = path.resolve(process.env.INTEGRATION_APP);
        console.log(`Debug run with a previously built integration fixture: ${artifact}. Device: ${udid}`);
    } else {
        console.log(`Building npm-packed plugin in a fresh Cordova ${platform} fixture. Device: ${udid}`);
        const pack = JSON.parse(execFileSync('npm', ['pack', '--ignore-scripts', '--json', '--pack-destination', work], { cwd: root, encoding: 'utf8' }));
        cli(['create', app, appId, 'YandexAdsIntegration'], work);
        fs.cpSync(path.join(directory, 'fixture'), path.join(app, 'www'), { recursive: true });
        const config = path.join(app, 'config.xml');
        // Only test instrumentation: expose the actual WebView to the Appium debugger.
        fs.writeFileSync(config, fs.readFileSync(config, 'utf8').replace('</widget>', '    <preference name="InspectableWebview" value="true" />\n</widget>'));
        cli(['platform', 'add', `${platform}@latest`]);
        command('tar', ['-xzf', path.join(work, pack[0].filename), '-C', work]);
        cli(['plugin', 'add', path.join(work, 'package')]);
        cli(['build', platform, '--debug', ...(platform === 'ios' ? ['--emulator'] : [])]);
        artifact = platform === 'android'
        ? path.join(app, 'platforms/android/app/build/outputs/apk/debug/app-debug.apk')
        : path.join(app, 'platforms/ios/build/Debug-iphonesimulator/YandexAdsIntegration.app');
    }
    assert(fs.existsSync(artifact), `Build output not found: ${artifact}`);
    fs.writeFileSync(path.join(output, 'environment.json'), JSON.stringify({
        platform, udid, app, artifact, reusedBuild: Boolean(process.env.INTEGRATION_APP), node: process.version
    }, null, 2));
    const port = await freePort();
    const serverArgs = [appiumCli, '--address', '127.0.0.1', '--port', String(port), '--log-timestamp', '--log-no-colors'];
    if (platform === 'android') serverArgs.push('--allow-insecure', 'uiautomator2:chromedriver_autodownload');
    server = spawn(process.execPath, serverArgs, { cwd: directory, env: { ...process.env, APPIUM_HOME: appiumHome }, stdio: ['ignore', appiumLog, appiumLog] });
    let ready = false;
    for (let attempt = 0; attempt < 120; attempt++) {
        assert(server.exitCode === null, `Appium exited with ${server.exitCode}; see appium.log`);
        try { ready = (await (await fetch(`http://127.0.0.1:${port}/status`)).json()).value.ready; } catch (_) { /* Server is starting. */ }
        if (ready) break;
        await pause(500);
    }
    assert(ready, 'Appium did not become ready; see appium.log');
    driver = await remote({
        hostname: '127.0.0.1', port, logLevel: 'error', connectionRetryCount: 0, connectionRetryTimeout: 240000,
        capabilities: {
            platformName: platform === 'android' ? 'Android' : 'iOS',
            'appium:automationName': platform === 'android' ? 'UiAutomator2' : 'XCUITest',
            'appium:udid': udid,
            'appium:app': artifact,
            'appium:enforceAppInstall': true,
            'appium:newCommandTimeout': 240,
            ...(platform === 'android' ? { 'appium:autoGrantPermissions': true } : { 'appium:showXcodeLog': true, 'appium:wdaLaunchTimeout': 180000 })
        }
    });
    for (const test of selected) {
        const result = { name: test.name, status: 'passed', duration: 0 };
        const started = Date.now();
        const testOutput = path.join(output, test.name);
        fs.mkdirSync(testOutput, { recursive: true });
        const device = new Device(driver, platform, appId, testOutput, artifact);
        try {
            await device.fresh(test.name, test.coldStart);
            await test.run(device);
            assert.deepEqual((await device.snapshot()).errors, []);
        } catch (error) {
            result.status = 'failed';
            result.error = error.stack || String(error);
        } finally {
            try { fs.writeFileSync(path.join(testOutput, 'events.json'), JSON.stringify(await device.snapshot(), null, 2)); } catch (error) { result.diagnosticsError = String(error); }
            try { await device.capture(result.status, !test.name.startsWith('fullscreen-')); } catch (error) { result.captureError = String(error); }
            result.duration = Date.now() - started;
            results.push(result);
            console.log(`${result.status.toUpperCase()} ${test.name} (${(result.duration / 1000).toFixed(1)}s)${result.error ? `\n${result.error}` : ''}`);
            report();
        }
    }
} catch (error) {
    fatal = error;
    console.error(error);
} finally {
    try { await driver?.deleteSession(); } catch (error) { console.error(`Session cleanup: ${error}`); }
    if (server) {
        server.kill('SIGTERM');
        for (let attempt = 0; attempt < 20 && server.exitCode === null; attempt++) await pause(250);
        if (server.exitCode === null) server.kill('SIGKILL');
    }
    nativeLogger?.kill('SIGTERM');
    if (simulatorStarted) {
        try { command('xcrun', ['simctl', 'shutdown', simulatorStarted]); } catch (error) { console.error(`Simulator cleanup: ${error}`); }
    }
    fs.closeSync(buildLog);
    fs.closeSync(appiumLog);
    fs.closeSync(nativeLog);
    report();
}
