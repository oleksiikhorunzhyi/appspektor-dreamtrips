package com.worldventures.dreamtrips.presentation;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.model.config.URLS;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;


public class WebViewFragmentPresentation extends BasePresentation<BasePresentation.View> {

    public WebViewFragmentPresentation(View view) {
        super(view);
    }

    public void track(State state) {
    switch (state) {
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




}
