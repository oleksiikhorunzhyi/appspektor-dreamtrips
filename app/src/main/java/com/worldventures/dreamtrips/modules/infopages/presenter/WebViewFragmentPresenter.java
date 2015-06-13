package com.worldventures.dreamtrips.modules.infopages.presenter;


import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.utils.LocaleUtils;
import com.worldventures.dreamtrips.core.utils.events.WebViewReloadEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

import javax.inject.Inject;


public class WebViewFragmentPresenter<T extends WebViewFragmentPresenter.View> extends Presenter<T> {

    @Inject
    StaticPageProvider staticPageProvider;
    @Inject
    StaticPageHolder staticPageHolder;
    @Inject
    LocalesHolder localesStorage;

    private final String url;

    public WebViewFragmentPresenter(String url) {
        this.url = url;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        view.load(getLocalizedUrl());
    }

    protected String getLocalizedUrl() {
        return LocaleUtils.substituteActualLocale(context, url, localesStorage);
    }

    public void onEvent(WebViewReloadEvent event) {
        if (view instanceof StaticInfoFragment.TrainingVideosFragment
                || view instanceof StaticInfoFragment.EnrollRepFragment
                || view instanceof StaticInfoFragment.EnrollFragment) {
            onReload();
        }
    }

    public void onReload() {
        view.reload(getLocalizedUrl());
    }

    public void track(Route route) {
        switch (route) {
            case TERMS_OF_SERVICE:
                TrackingHelper.service(getAccountUserId());
                break;
            case FAQ:
                TrackingHelper.faq(getAccountUserId());
                break;
            case COOKIE_POLICY:
                TrackingHelper.cookie(getAccountUserId());
                break;
            case PRIVACY_POLICY:
                TrackingHelper.privacy(getAccountUserId());
                break;
            case OTA:
                TrackingHelper.ota(getAccountUserId());
                break;
        }
    }

    public interface View extends Presenter.View {
        void load(String localizedUrl);
        void reload(String localizedUrl);
    }

}
