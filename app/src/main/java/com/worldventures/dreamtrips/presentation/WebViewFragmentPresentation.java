package com.worldventures.dreamtrips.presentation;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.model.config.URLS;


public class WebViewFragmentPresentation<T extends BasePresentation.View> extends BasePresentation<T> {

    public WebViewFragmentPresentation(T view) {
        super(view);
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
