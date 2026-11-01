import path from 'node:path';
import { fileURLToPath } from 'node:url';
export const directory = path.dirname(fileURLToPath(import.meta.url));
export const root = path.resolve(directory, '../..');
export const appiumHome = path.join(root, '.cache/integration/appium');
export const appiumCli = path.join(directory, 'node_modules/appium/index.js');
export const drivers = {
    android: { name: 'uiautomator2', version: '8.7.3' },
    ios: { name: 'xcuitest', version: '12.22.8' }
};
