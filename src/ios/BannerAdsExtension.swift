import YandexMobileAds

let EVENT_BANNER_DID_LOAD = "bannerDidLoad"
let EVENT_BANNER_DID_CLICK = "bannerDidClick"
let EVENT_BANNER_DID_TRACK_IMPRESSION_WITH = "bannerDidTrackImpressionWith"
let EVENT_BANNER_FAILED_TO_LOAD = "bannerFailedToLoad"
let EVENT_BANNER_WILL_LEAVE_APPLICATION = "bannerWillLeaveApplication"
let EVENT_BANNER_WILL_SHOW = "bannerWillShow"
let EVENT_BANNER_DID_DISMISS = "bannerDidDismiss"

extension YandexAdsPlugin {
    func getBannerAdView() -> YMAAdView {
        if self.bannerAdViewCache != nil {
            return self.bannerAdViewCache!
        }

        let width = self.webView.safeAreaLayoutGuide.layoutFrame.width
        var adSize = YMABannerAdSize.stickySize(withContainerWidth: width)

        if (self.bannerSize != nil && self.bannerSize?["width"] != nil && self.bannerSize?["height"] != nil) {
            adSize = YMABannerAdSize.inlineSize(withWidth: self.bannerSize?["width"] as! CGFloat, maxHeight: self.bannerSize?["height"] as! CGFloat)
        }

        let adView = YMAAdView(adUnitID: self.bannerBlockId!, adSize: adSize)

        adView.delegate = self

        self.bannerAdViewCache = adView

        return adView
    }

    @objc(loadBanner:)
    func loadBanner(command: CDVInvokedUrlCommand) {
        if self.bannerBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        self.getBannerAdView().loadAd()

        self.sendResult(command: command);
    }

    @objc(showBanner:)
    func showBanner(command: CDVInvokedUrlCommand) {
        if self.bannerBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        let banner = self.getBannerAdView();

        if (self.bannerSize != nil && self.bannerSize?["width"] != nil && self.bannerSize?["height"] != nil) {
            self.showInlineBanner(banner: banner)
        } else {
            self.showOverlapBanner(banner: banner)
        }

        self.sendResult(command: command);
    }

    @objc(reloadBanner:)
    func reloadBanner(command: CDVInvokedUrlCommand) {
        if self.bannerBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        // hide banner
        self.getBannerAdView().removeFromSuperview()

        self.getBannerAdView().delegate = nil
        self.bannerAdViewCache = nil

        // load new banner
        self.bannerReloaded = true

        let banner = self.getBannerAdView();
        banner.loadAd()

        // show banner
        if (self.bannerSize != nil && self.bannerSize?["width"] != nil && self.bannerSize?["height"] != nil) {
            banner.displayAtTop(in: self.bannerStackView!)

            NSLayoutConstraint.activate([
                self.bannerStackView!.trailingAnchor.constraint(equalTo: banner.trailingAnchor, constant: 0.0),
                self.bannerStackView!.bottomAnchor.constraint(equalTo: banner.bottomAnchor, constant: 0.0),
            ])
        } else {
            self.showOverlapBanner(banner: banner)
        }

        self.sendResult(command: command);
    }

    func showOverlapBanner(banner: YMAAdView) {
        if self.bannerAtTop != nil && self.bannerAtTop == true {
            banner.displayAtTop(in: webView)
        } else {
            banner.displayAtBottom(in: webView)
        }
    }

    func showInlineBanner(banner: YMAAdView) {
        let stackview: UIStackView = {
            let view = UIStackView()
            view.axis = .vertical
            view.distribution = .fill
            view.translatesAutoresizingMaskIntoConstraints = false
            return view
        }()

        self.stackViewInlineBannerView = stackview

        self.bannerStackView = {
            let view = UIView()
            view.backgroundColor = .black
            view.translatesAutoresizingMaskIntoConstraints = false
            return view
        }()

        self.superView?.addSubview(stackview)
        webView.removeFromSuperview()

        if self.bannerAtTop != nil && self.bannerAtTop == true {
            stackview.addArrangedSubview(self.bannerStackView!)
            stackview.addArrangedSubview(webView)
        } else {
            stackview.addArrangedSubview(webView)
            stackview.addArrangedSubview(self.bannerStackView!)
        }

        banner.displayAtTop(in: self.bannerStackView!)

        NSLayoutConstraint.activate([
            stackview.leadingAnchor.constraint(equalTo: self.superView!.leadingAnchor, constant: 0.0),
            stackview.trailingAnchor.constraint(equalTo: self.superView!.trailingAnchor, constant: 0.0),
            stackview.topAnchor.constraint(equalTo: self.superView!.topAnchor, constant: 0.0),
            stackview.bottomAnchor.constraint(equalTo: self.superView!.bottomAnchor, constant: 0.0),
            self.bannerStackView!.trailingAnchor.constraint(equalTo: banner.trailingAnchor, constant: 0.0),
            self.bannerStackView!.bottomAnchor.constraint(equalTo: banner.bottomAnchor, constant: 0.0),
        ])
    }

    @objc(hideBanner:)
    func hideBanner(command: CDVInvokedUrlCommand) {
        if self.bannerBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        if (self.bannerSize != nil && self.bannerSize?["width"] != nil && self.bannerSize?["height"] != nil) {
            self.stackViewInlineBannerView?.removeFromSuperview()
            self.superView!.addSubview(webView)

            NSLayoutConstraint.activate([
                webView.leadingAnchor.constraint(equalTo: self.superView!.leadingAnchor, constant: 0.0),
                webView.trailingAnchor.constraint(equalTo: self.superView!.trailingAnchor, constant: 0.0),
                webView.topAnchor.constraint(equalTo: self.superView!.topAnchor, constant: 0.0),
                webView.bottomAnchor.constraint(equalTo: self.superView!.bottomAnchor, constant: 0.0),
            ])
        } else {
            self.getBannerAdView().removeFromSuperview()
        }

        self.getBannerAdView().delegate = nil
        self.bannerAdViewCache = nil

        self.sendResult(command: command);
    }
}

extension YandexAdsPlugin: YMAAdViewDelegate {
    func adViewDidLoad(_ adView: YMAAdView) {
        if (self.bannerReloaded == nil || self.bannerReloaded == false) {
            self.emitWindowEvent(event: EVENT_BANNER_DID_LOAD)
        }
        self.bannerReloaded = false
    }

    func adViewDidClick(_ adView: YMAAdView) {
        self.emitWindowEvent(event: EVENT_BANNER_DID_CLICK)
    }

    func adView(_ adView: YMAAdView, didTrackImpressionWith impressionData: YMAImpressionData?) {
        self.emitWindowEvent(event: EVENT_BANNER_DID_TRACK_IMPRESSION_WITH)
    }

    func adViewDidFailLoading(_ adView: YMAAdView, error: Error) {
        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_BANNER_FAILED_TO_LOAD, data: data)
    }

    func adViewWillLeaveApplication(_ adView: YMAAdView) {
        self.emitWindowEvent(event: EVENT_BANNER_WILL_LEAVE_APPLICATION)
    }

    func adView(_ adView: YMAAdView, willPresentScreen viewController: UIViewController?) {
        self.emitWindowEvent(event: EVENT_BANNER_WILL_SHOW)
    }

    func adView(_ adView: YMAAdView, didDismissScreen viewController: UIViewController?) {
        self.emitWindowEvent(event: EVENT_BANNER_DID_DISMISS)
    }
}
