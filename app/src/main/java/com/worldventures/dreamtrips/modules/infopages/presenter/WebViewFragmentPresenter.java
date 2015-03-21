package com.worldventures.dreamtrips.modules.infopages.presenter;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;
import com.worldventures.dreamtrips.core.model.config.URLS;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;


public class WebViewFragmentPresenter<T extends BasePresenter.View> extends BasePresenter<T> {

    public WebViewFragmentPresenter(T view) {
        super(view);
    }

    public void track(Route route) {
        switch (route) {
            case TERMS_OF_SERVICE:
                AdobeTrackingHelper.service(getUserId());
                break;
            case FAQ:
                AdobeTrackingHelper.faq(getUserId());
                break;
            case COOKIE_POLICY:
                AdobeTrackingHelper.cookie(getUserId());
                break;
            case PRIVACY_POLICY:
                AdobeTrackingHelper.privacy(getUserId());
                break;
        }
    }

    public String etEnrollUrl() {
        URLS urls = appSessionHolder.get().get().getGlobalConfig().getUrls();
        if (BuildConfig.DEBUG) {
            return urls.getQA().getEnrollMemeberURL();
        } else {
            return urls.getProduction().getEnrollMemeberURL();
        }
    }


    public S3GlobalConfig getConfig() {
        return appSessionHolder.get().get().getGlobalConfig();
    }


    public UserSession getCurrentUser() {
        return appSessionHolder.get().get();
    }

}