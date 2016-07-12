package com.worldventures.dreamtrips.modules.infopages.presenter;


import android.util.Base64;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import timber.log.Timber;


public class WebViewFragmentPresenter<T extends WebViewFragmentPresenter.View> extends Presenter<T> {

    @Inject
    StaticPageProvider staticPageProvider;
    @Inject
    StaticPageHolder staticPageHolder;
    @Inject
    LocaleHelper localeHelper;

    private final String url;
    private boolean inErrorState;

    public WebViewFragmentPresenter(String url) {
        this.url = url;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        load();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (inErrorState) load();
    }

    protected void load() {
        view.load(getLocalizedUrl());
    }

    protected void reload() {
        view.reload(getLocalizedUrl());
    }

    protected String getLocalizedUrl() {
        return url
                .replaceAll("\\{locale\\}", localeHelper.getDefaultLocaleFormatted())
                .replaceAll("\\{language\\}", localeHelper.getDefaultLocale().getLanguage())
                .replaceAll("\\{country\\}", localeHelper.getDefaultLocale().getCountry())
                .replaceAll("\\{BASE64_ENCODED_LOCALE\\}", getBase64String(localeHelper.getDefaultLocaleFormatted()));
    }

    public void onReload() {
        reload();
    }

    public void setInErrorState(boolean inErrorState) {
        this.inErrorState = inErrorState;
    }

    public void track(Route route) {
        switch (route) {
            case TERMS_OF_SERVICE:
                TrackingHelper.service(getAccountUserId());
                break;
            case FAQ:
                TrackingHelper.actionFaq();
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
            case ENROLL_MEMBER:
                TrackingHelper.enrollMember(getAccountUserId());
                break;
            case ENROLL_MERCHANT:
                TrackingHelper.enrollMerchant(getAccountUserId());
                break;
        }
    }

    private String getBase64String(String string) {
        try {
            return Base64.encodeToString(string.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            Timber.e(e.toString());
            return "";
        }
    }

    public interface View extends Presenter.View {
        void load(String localizedUrl);

        void reload(String localizedUrl);

        void setRefreshing(boolean refreshing);

        void showError(int code);
    }

}
