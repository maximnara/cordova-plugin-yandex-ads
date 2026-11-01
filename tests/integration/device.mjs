import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';

export const pause = ms => new Promise(resolve => setTimeout(resolve, ms));

export class Device {
    constructor(driver, platform, appId, output, artifact) {
        Object.assign(this, { driver, platform, appId, output, artifact });
        this.context = null;
    }

    async selectFixture() {
        if (await this.driver.execute(() => Boolean(window.integrationFixture?.ready))) return true;
        // Android may expose the SDK's ad WebViews as pages in the same context.
        for (const handle of await this.driver.getWindowHandles()) {
            await this.driver.switchToWindow(handle);
            if (await this.driver.execute(() => Boolean(window.integrationFixture?.ready))) return true;
        }
        return false;
    }

    async web() {
        if (this.context) {
            try {
                await this.driver.switchContext(this.context);
                if (await this.selectFixture()) return;
            } catch (_) { this.context = null; }
        }
        const deadline = Date.now() + 90000;
        while (Date.now() < deadline) {
            assert.notEqual(await this.driver.queryAppState(this.appId), 1, 'The test app stopped or crashed; inspect native.log');
            const contexts = await this.driver.getContexts();
            for (const item of contexts) {
                const id = typeof item === 'string' ? item : item.id;
                if (!id.startsWith('WEBVIEW')) continue;
                try {
                    await this.driver.switchContext(id);
                    if (await this.selectFixture()) {
                        this.context = id;
                        return;
                    }
                } catch (_) { /* SDK ad WebViews are not the Cordova fixture. */ }
            }
            await pause(500);
        }
        throw new Error('No inspectable Cordova WebView with a ready integration fixture');
    }

    async fresh(name, coldStart = false) {
        await this.driver.switchContext('NATIVE_APP');
        await this.driver.terminateApp(this.appId);
        if (coldStart) {
            await this.driver.removeApp(this.appId);
            await this.driver.installApp(this.artifact);
        }
        await this.driver.activateApp(this.appId);
        this.context = null;
        await this.web();
        // Allow the native init callback deadline to expire before WebDriver's script deadline.
        await this.driver.setTimeout({ script: 95000 });
        await this.driver.execute(name => {
            window.integrationFixture.testName = name;
            document.getElementById('status').textContent = name.startsWith('invalid-id-')
                ? `${name} — expected SDK load failure` : name;
        }, name);
    }

    async snapshot() {
        await this.web();
        return this.driver.execute(() => window.integrationFixture.snapshot());
    }

    async call(method, ...args) {
        await this.web();
        // First SDK initialization on a cold simulator may take longer than bridge commands.
        const callbackTimeout = method === 'init' ? 90000 : 10000;
        const envelope = await this.driver.execute(async (method, args, timeout) => {
            let timer;
            try {
                const response = await Promise.race([
                    window.integrationFixture.call(method, args),
                    new Promise(resolve => { timer = setTimeout(() => resolve({ ok: false, error: { code: 'callback_timeout', message: `${method}: no native callback in ${timeout / 1000}s` } }), timeout); })
                ]);
                // WebDriver reserves a top-level `error` field for protocol failures.
                return { response };
            } finally { clearTimeout(timer); }
        }, method, args, callbackTimeout);
        return envelope.response;
    }

    async ok(method, ...args) {
        const result = await this.call(method, ...args);
        assert.equal(result.ok, true, `${method}: ${JSON.stringify(result.error)}`);
    }

    async mark() { return (await this.snapshot()).events.length; }

    async event(name, since, failures = [], timeout = 90000) {
        const deadline = Date.now() + timeout;
        while (Date.now() < deadline) {
            const snapshot = await this.snapshot();
            assert.deepEqual(snapshot.errors, [], 'Uncaught error in the real Cordova app');
            const events = snapshot.events.slice(since);
            const error = events.find(event => failures.includes(event.name));
            assert(!error, `Unexpected native event: ${JSON.stringify(error)}`);
            const found = events.find(event => event.name === name);
            if (found) return found;
            await pause(400);
        }
        throw new Error(`Timed out waiting for ${name}. Events: ${JSON.stringify((await this.snapshot()).events.slice(since))}`);
    }

    async capture(label, includeSource = true) {
        await this.driver.switchContext('NATIVE_APP');
        await this.driver.saveScreenshot(path.join(this.output, `${label}.png`));
        if (includeSource) fs.writeFileSync(path.join(this.output, `${label}.xml`), await this.driver.getPageSource());
    }

    async background() {
        await this.driver.switchContext('NATIVE_APP');
        await this.driver.background(2);
        await this.web();
    }
}
