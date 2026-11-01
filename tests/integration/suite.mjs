import assert from 'node:assert/strict';
import { createRequire } from 'node:module';
import { pause } from './device.mjs';
const require = createRequire(import.meta.url);
const { events } = require('../../www/yandexads.js');

export const formats = [
    { name: 'banner', key: 'bannerBlockId', id: 'demo-banner-yandex', load: 'loadBanner', show: 'showBanner', hide: 'hideBanner' },
    { name: 'interstitial', key: 'interstitialBlockId', id: 'demo-interstitial-yandex', load: 'loadInterstitial', show: 'showInterstitial' },
    { name: 'rewarded', key: 'rewardedBlockId', id: 'demo-rewarded-yandex', load: 'loadRewardedVideo', show: 'showRewardedVideo' },
    { name: 'openAppAds', key: 'openAppBlockId', id: 'demo-appopenad-yandex', load: 'loadOpenAppAds', show: 'showOpenAppAds' },
    { name: 'feed', key: 'feedBlockId', id: 'demo-feed-yandex', load: 'loadFeed', show: 'showFeed', hide: 'hideFeed', androidOnly: true },
    { name: 'instream', key: 'instreamBlockId', id: 'demo-instream-vmap-yandex', load: 'loadInstream', show: 'showInstream', hide: 'hideInstream', androidOnly: true }
];

async function init(t, options = {}, invalid = false) {
    await t.ok('setUserConsent', false);
    await t.ok('init', {
        ...Object.fromEntries(formats.map(format => [format.key, invalid ? `invalid-integration-${format.name}` : format.id])),
        options
    });
    assert.equal((await t.snapshot()).initialized, true);
}

async function load(t, format) {
    const mark = await t.mark();
    await t.ok(format.load);
    await t.event(events[format.name].loaded, mark, [events[format.name].failedToLoad]);
    return mark;
}

export function cases(platform) {
    const supported = formats.filter(format => !format.androidOnly || platform === 'android');
    const tests = [];
    const add = (name, run, coldStart = false) => tests.push({ name, run, coldStart });

    for (const format of supported) {
        add(`cold-start-${format.name}`, async t => {
            await init(t);
            await load(t, format);
            if (format.hide) await t.ok(format.hide);
        }, true);
    }

    add('bridge-and-consent', async t => {
        assert.equal((await t.snapshot()).initialized, false);
        await t.ok('setUserConsent', false);
        await t.ok('setUserConsent', true);
        const invalid = await t.call('init', {});
        assert.equal(invalid.ok, false, 'JS argument validation must reject an empty init');
        await init(t);
        await t.ok('setUserConsent', true);
        await t.ok('setUserConsent', false);
    });

    for (const format of supported) {
        add(`before-init-${format.name}`, async t => {
            for (const method of [format.load, format.show, format.hide, ...(format.name === 'banner' ? ['reloadBanner'] : [])].filter(Boolean)) {
                const result = await t.call(method);
                assert.equal(result.ok, false, `${method} before init must reject`);
                assert.equal(result.error.code, 'plugin_not_initialized', `${method}: native code must reject immediately with a structured error`);
            }
        });
        add(`invalid-id-${format.name}`, async t => {
            await init(t, {}, true);
            const mark = await t.mark();
            await t.ok(format.load);
            const event = await t.event(events[format.name].failedToLoad, mark, [events[format.name].loaded]);
            assert.equal(typeof event.data.message, 'string', 'SDK error payload must cross the bridge as an object');
            assert(event.data.message.length > 0, 'The SDK failure reason must be preserved');
            if (format.name === 'openAppAds') {
                assert(!(await t.snapshot()).events.slice(mark).some(row => row.name === events.interstitial.failedToLoad), 'AppOpen must not emit an Interstitial failure');
            }
            if (format.hide) await t.ok(format.hide);
        });
    }

    for (const inline of [false, true]) for (const top of [false, true]) {
        add(`banner-${inline ? 'inline' : 'sticky'}-${top ? 'top' : 'bottom'}`, async t => {
            await init(t, { bannerAtTop: top, ...(inline ? { bannerSize: { width: 320, height: 100 } } : {}) });
            const initial = (await t.snapshot()).viewport;
            await load(t, formats[0]);
            await t.ok('showBanner');
            await t.capture('banner-visible');
            if (inline) assert((await t.snapshot()).viewport.height < initial.height, 'Inline banner must reserve native layout space');
            await t.ok('reloadBanner');
            // reloadBanner has no loaded callback; impression is not a completion signal for this test.
            await t.capture('banner-reloaded');
            await t.ok('hideBanner');
            await t.ok('hideBanner');
            await pause(500);
            assert(Math.abs((await t.snapshot()).viewport.height - initial.height) <= 2, 'Hiding a banner must restore the Cordova viewport');
            await load(t, formats[0]);
            await t.ok('showBanner');
            await t.ok('hideBanner');
        });
    }

    for (const format of formats.filter(format => !format.hide)) {
        add(`fullscreen-${format.name}`, async t => {
            await init(t);
            const names = events[format.name];
            await load(t, format);
            const mark = await t.mark();
            await t.ok(format.show);
            await t.event(names.shown, mark, [names.failedToShow]);
            // A screenshot is enough evidence here; UI-tree idle waits stall on playing videos.
            await t.capture('visible', false);
            const rows = (await t.snapshot()).events.slice(mark);
            assert.equal(rows.filter(row => row.name === names.shown).length, 1, 'The SDK must confirm presentation once');
        });
    }

    add('background-and-resume', async t => {
        await init(t);
        await load(t, formats[0]);
        await t.ok('showBanner');
        const mark = await t.mark();
        await t.background();
        await t.event('pause', mark, [], 15000);
        await t.event('resume', mark, [], 15000);
        await t.ok('hideBanner');
        await load(t, formats[1]);
    });

    if (platform === 'android') {
        add('feed-show-hide-reload', async t => {
            await init(t);
            const format = formats.find(format => format.name === 'feed');
            for (let cycle = 0; cycle < 2; cycle++) {
                await load(t, format);
                const mark = await t.mark();
                await t.ok('showFeed');
                await t.event(events.feed.impression, mark, [events.feed.failedToLoad]);
                await t.capture(`visible-${cycle}`);
                await t.ok('hideFeed');
                await t.ok('hideFeed');
            }
        });

        add('instream-playback-and-cleanup', async t => {
            await init(t);
            const format = formats.find(format => format.name === 'instream');
            await load(t, format);
            let mark = await t.mark();
            await t.ok('showInstream');
            await t.event(events.instream.adStarted, mark, [events.instream.error]);
            await t.capture('playing');
            await t.event(events.instream.completed, mark, [events.instream.error], 180000);
            await t.ok('hideInstream');
            await t.ok('hideInstream');
            await load(t, format);
            mark = await t.mark();
            await t.ok('showInstream');
            await t.event(events.instream.adStarted, mark, [events.instream.error]);
            await t.ok('hideInstream');
            await t.ok('hideInstream');
            await load(t, formats[0]);
        });
    }
    return tests;
}
