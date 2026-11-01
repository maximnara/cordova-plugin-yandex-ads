import fs from 'node:fs';
import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import { appiumCli, appiumHome, drivers } from './paths.mjs';

const platform = process.argv[2];
assert(drivers[platform], 'Usage: npm run test:integration:setup -- android|ios');
assert(platform !== 'ios' || process.platform === 'darwin', 'iOS tests require macOS and Xcode');
assert(fs.existsSync(appiumCli), 'Run npm ci --prefix tests/integration first');
fs.mkdirSync(appiumHome, { recursive: true });
const env = { ...process.env, APPIUM_HOME: appiumHome };
const installed = JSON.parse(execFileSync(process.execPath, [appiumCli, 'driver', 'list', '--installed', '--json'], {
    env, encoding: 'utf8', stdio: ['ignore', 'pipe', 'inherit']
}));
const { name, version } = drivers[platform];
if (installed[name]?.version !== version) {
    if (installed[name]) execFileSync(process.execPath, [appiumCli, 'driver', 'uninstall', name], { env, stdio: 'inherit' });
    execFileSync(process.execPath, [appiumCli, 'driver', 'install', `${name}@${version}`], { env, stdio: 'inherit' });
}
console.log(`Ready: ${name}@${version}. APPIUM_HOME=${appiumHome}`);
