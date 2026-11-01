/* The real plugin is installed by Cordova. No replacement bridge or SDK. */
window.integrationFixture = {
    ready: false,
    events: [],
    errors: [],
    snapshot() {
        return {
            ready: this.ready,
            platform: cordova.platformId,
            testName: this.testName,
            initialized: window.YandexAds?.isInitialized(),
            events: this.events,
            errors: this.errors,
            viewport: { width: innerWidth, height: innerHeight }
        };
    },
    async call(method, args) {
        try {
            await YandexAds[method](...args);
            return { ok: true };
        } catch (error) {
            return { ok: false, error: typeof error === 'object' ? {
                ...error, message: error.message || String(error)
            } : { message: String(error) } };
        }
    }
};

window.addEventListener('error', event => integrationFixture.errors.push(event.message));
window.addEventListener('unhandledrejection', event => integrationFixture.errors.push(String(event.reason)));

document.addEventListener('deviceready', () => {
    const names = new Set(Object.values(YandexAds.events).flatMap(group => Object.values(group)));
    function record(event) {
        const data = {};
        for (const key of Object.keys(event)) {
            if (['type', 'target', 'currentTarget', 'isTrusted'].includes(key)) continue;
            try { data[key] = JSON.parse(JSON.stringify(event[key])); } catch (_) { /* DOM properties are not payloads. */ }
        }
        integrationFixture.events.push({ name: event.type, data, at: Date.now() });
        document.getElementById('log').textContent = integrationFixture.events.slice(-15).map(row => row.name).join('\n');
    }
    for (const name of names) window.addEventListener(name, record);
    for (const name of ['pause', 'resume', 'backbutton']) document.addEventListener(name, record);
    // Feed and InStream have no native close API event; use the public hide commands on Back.
    document.addEventListener('backbutton', () => {
        if (cordova.platformId === 'android') {
            YandexAds.hideFeed().catch(() => {});
            YandexAds.hideInstream().catch(() => {});
        }
    });
    integrationFixture.ready = true;
    document.getElementById('status').textContent = `Ready: ${cordova.platformId}`;
}, { once: true });
