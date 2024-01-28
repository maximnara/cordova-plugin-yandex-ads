let YandexAds = (function () {
    let initialized = false;

    return {
        events: {
            interstitial: {
                loaded: 'interstitialDidLoad',
                failedToLoad: 'interstitialFailedToLoad',
                shown: 'interstitialDidShow',
                failedToShow: 'interstitialDidFailToShowWithError',
                dismissed: 'interstitialDidDismiss',
                clicked: 'interstitialDidClick',
                impression: 'interstitialDidTrackImpressionWith',
            },
            rewarded: {
                loaded: 'rewardedDidLoad',
                failedToLoad: 'rewardedFailedToLoad',
                rewarded: 'rewardedDidReward',
                shown: 'rewardedDidShow',
                failedToShow: 'rewardedDidFailToShowWithError',
                dismissed: 'rewardedDidDismiss',
                clicked: 'rewardedDidClick',
                impression: 'rewardedDidTrackImpressionWith',
            },
            openAppAds: {
                loaded: 'appOpenDidLoad',
                failedToLoad: 'appOpenFailedToLoad',
                shown: 'appOpenDidShow',
                failedToShow: 'appOpenDidFailToShowWithError',
                dismissed: 'appOpenDidDismiss',
                clicked: 'appOpenDidClick',
                impression: 'appOpenDidTrackImpressionWith',
            },
            banner: {
                loaded: 'bannerDidLoad',
                failedToLoad: 'bannerFailedToLoad',
                clicked: 'bannerDidClick',
                impression: 'bannerDidTrackImpressionWith',
                leftApplication: 'bannerWillLeaveApplication',
            },
        },

        /**
         * Returns the state of initialization
         */
        isInitialized: function isInitialized()
        {
            return initialized;
        },

        /**
         * Initializes iron source
         * @param {Function} params.onSuccess - optional on success callback
         */
        init: function init(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                if (params.hasOwnProperty('rewardedBlockId') === false && params.hasOwnProperty('interstitialBlockId') === false && params.hasOwnProperty('bannerBlockId') === false)
                {
                    throw new Error('YandexAds::init - rewardedBlockId or interstitialBlockId or bannerBlockId is required');
                }

                callPlugin('run', [params.rewardedBlockId, params.interstitialBlockId, params.bannerBlockId, params.openAppBlockId, params.options || {}], function ()
                {

                    initialized = true;

                    resolve();

                }, reject);
            });
        },

        /**
         * Shows rewarded video
         * @param {String} params.placement - optional placement name
         * @param {Function} params.onSuccess - optional on success callback
         * @param {Function} param.onFailure - optional on failure callback
         */
        loadRewardedVideo: function loadRewardedVideo(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('loadRewardedVideo', [], resolve, reject);
            });
        },

        /**
         * Shows rewarded video
         * @param {String} params.placement - optional placement name
         * @param {Function} params.onSuccess - optional on success callback
         * @param {Function} param.onFailure - optional on failure callback
         */
        showRewardedVideo: function showRewardedVideo(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('showRewardedVideo', [], resolve, reject);
            });
        },

        /**
         * Loads open app ads
         * @param {String} params.placement - optional placement name
         * @param {Function} params.onSuccess - optional on success callback
         * @param {Function} param.onFailure - optional on failure callback
         */
        loadOpenAppAds: function loadRewardedVideo(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('loadOpenAppAds', [], resolve, reject);
            });
        },

        /**
         * Shows open app ads
         * @param {String} params.placement - optional placement name
         * @param {Function} params.onSuccess - optional on success callback
         * @param {Function} param.onFailure - optional on failure callback
         */
        showOpenAppAds: function showRewardedVideo(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('showOpenAppAds', [], resolve, reject);
            });
        },

        /**
         * Shows banner if avaialble
         * @param {Function} params.onSuccess
         */
        showBanner: function showBanner(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('showBanner', [], resolve, reject);
            });
        },

        /**
         * Shows banner if avaialble
         * @param {Function} params.onSuccess
         */
        hideBanner: function showBanner(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('hideBanner', [], resolve, reject);
            });
        },

        /**
         * Loads interstitial
         */
        loadBanner: async function loadBanner(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('loadBanner', [], resolve, reject);
            });
        },

        /**
         * Loads interstitial
         */
        loadInterstitial: function loadInterstitial(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('loadInterstitial', [], resolve, reject);
            });
        },

        /**
         * Show interstitial
         */
        showInterstitial: function showInterstitial(params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('showInterstitial', [], resolve, reject);
            });
        },

        setUserConsent: function (value, params)
        {
            return new Promise((resolve, reject) => {
                params = defaults(params, {});

                callPlugin('setUserConsent', [value], resolve, reject);
            });
        },
    }
})();



/**
 * Helper function to call cordova plugin
 * @param {String} name - function name to call
 * @param {Array} params - optional params
 * @param {Function} onSuccess - optional on sucess function
 * @param {Function} onFailure - optional on failure functioin
 */
function callPlugin(name, params, onSuccess, onFailure)
{
    cordova.exec(function callPluginSuccess(result)
    {

        if (isFunction(onSuccess))
        {
            onSuccess(result);
        }
    }, function callPluginFailure(error)
    {
        if (isFunction(onFailure))
        {
            onFailure(error)
        }
    }, 'YandexAdsPlugin', name, params);
}

/**
 * Helper function to check if a function is a function
 * @param {Object} functionToCheck - function to check if is function
 */
function isFunction(functionToCheck)
{
    var getType = {};
    var isFunction = functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
    return isFunction === true;
}

/**
 * Helper function to do a shallow defaults (merge). Does not create a new object, simply extends it
 * @param {Object} o - object to extend
 * @param {Object} defaultObject - defaults to extend o with
 */
function defaults(o, defaultObject)
{
    if (typeof o === 'undefined')
    {
        return defaults({}, defaultObject);
    }

    for (var j in defaultObject)
    {
        if (defaultObject.hasOwnProperty(j) && o.hasOwnProperty(j) === false)
        {
            o[j] = defaultObject[j];
        }
    }

    return o;
}


if (typeof module !== undefined && module.exports)
{
    module.exports = YandexAds;
}
