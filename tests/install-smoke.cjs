// Maintainer regression test: a standard Cordova app must build after installing
// the packed plugin, without changing config.xml or adding build scripts.
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const assert = require('node:assert/strict');
const { execFileSync } = require('node:child_process');

const root = path.resolve(__dirname, '..');
const cli = require.resolve('cordova/bin/cordova');
const selected = process.argv.slice(2);
const platforms = selected.length ? selected : ['android', 'ios'];
assert(platforms.every(platform => ['android', 'ios'].includes(platform)), 'Use android and/or ios');
const temp = fs.mkdtempSync(path.join(os.tmpdir(), 'yandex-cordova-install-'));
const app = path.join(temp, 'app');

function cordova(args, cwd = app) {
    execFileSync(process.execPath, [cli, ...args], { cwd, stdio: 'inherit' });
}

try {
    const npm = process.platform === 'win32' ? 'npm.cmd' : 'npm';
    const pack = JSON.parse(execFileSync(npm, ['pack', '--ignore-scripts', '--json', '--pack-destination', temp], { cwd: root, encoding: 'utf8' }));
    cordova(['create', app, 'com.yandex.installtest', 'YandexInstallTest'], temp);
    const initialConfig = fs.readFileSync(path.join(app, 'config.xml'), 'utf8');
    cordova(['platform', 'add', ...platforms.map(platform => `${platform}@latest`)]);
    // Test the published package contents, not a symlink to the working tree.
    // Cordova's local-plugin resolver accepts a directory, not a .tgz path.
    execFileSync('tar', ['-xzf', path.join(temp, pack[0].filename), '-C', temp]);
    cordova(['plugin', 'add', path.join(temp, 'package')]);
    // Updating a local plugin must also work without resetting the platforms.
    cordova(['plugin', 'remove', 'cordova-plugin-yandex-ads', '--nosave']);
    cordova(['plugin', 'add', path.join(temp, 'package'), '--nosave']);
    for (const platform of platforms) {
        cordova(['build', platform, '--debug', ...(platform === 'ios' ? ['--emulator'] : [])]);
        if (platform === 'ios') {
            const info = path.join(app, 'platforms/ios/build/Debug-iphonesimulator/YandexInstallTest.app/Info.plist');
            const networks = JSON.parse(execFileSync('plutil', ['-extract', 'SKAdNetworkItems', 'json', '-o', '-', info], { encoding: 'utf8' }));
            const actualIds = networks.map(item => item.SKAdNetworkIdentifier);
            const expectedIds = [...fs.readFileSync(path.join(root, 'plugin.xml'), 'utf8').matchAll(/<string>([a-z0-9]{10}\.skadnetwork)<\/string>/g)].map(match => match[1]);
            assert(actualIds.includes('zq492l623r.skadnetwork'), 'Built iOS app must include the Yandex SKAdNetwork identifier');
            assert(expectedIds.every(id => actualIds.includes(id)), 'Built iOS app must include the complete bundled SKAdNetwork list');
            assert.equal(actualIds.length, new Set(actualIds).size, 'Reinstalling must not duplicate SKAdNetwork entries');
        }
    }
    assert.equal(fs.readFileSync(path.join(app, 'config.xml'), 'utf8'), initialConfig, 'Application config.xml must remain unchanged');
    assert(!fs.existsSync(path.join(app, 'hooks/before_build')), 'No application build hooks required');
    console.log(`Clean installation and build passed: ${platforms.join(', ')}. App: ${app}`);
} catch (error) {
    console.error(`Clean installation failed. Inspect: ${app}`);
    throw error;
}
