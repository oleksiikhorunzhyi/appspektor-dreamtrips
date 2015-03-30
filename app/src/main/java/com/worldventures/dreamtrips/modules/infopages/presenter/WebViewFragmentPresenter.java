package com.worldventures.dreamtrips.modules.infopages.presenter;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;


public class WebViewFragmentPresenter<T extends Presenter.View> extends Presenter<T> {

    public WebViewFragmentPresenter(T view) {
        super(view);
    }

    public void track(Route route) {
        switch (route) {
            case TERMS_OF_SERVICE:
                TrackingHelper.service(getUserId());
                break;
            case FAQ:
                TrackingHelper.faq(getUserId());
                break;
            case COOKIE_POLICY:
                TrackingHelper.cookie(getUserId());
                break;
            case PRIVACY_POLICY:
                TrackingHelper.privacy(getUserId());
                break;
        }
    }

    public String etEnrollUrl() {
        AppConfig.URLS urls = appSessionHolder.get().get().getGlobalConfig().getUrls();
        if (BuildConfig.DEBUG) {
            return urls.getQA().getEnrollMemeberURL();
        } else {
            return urls.getProduction().getEnrollMemeberURL();
        }
    }


    public AppConfig getConfig() {
        return appSessionHolder.get().get().getGlobalConfig();
    }


    public UserSession getCurrentUser() {
        return appSessionHolder.get().get();
    }

}
