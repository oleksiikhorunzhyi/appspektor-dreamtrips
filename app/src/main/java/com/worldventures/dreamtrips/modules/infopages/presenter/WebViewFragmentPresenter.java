package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

public class WebViewFragmentPresenter<T extends WebViewFragmentPresenter.View> extends Presenter<T> {

    @Inject LocaleHelper localeHelper;

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
        view.load(url);
    }

    protected void reload() {
        view.reload(url);
    }

    public void onReload() {
        reload();
    }

    public void setInErrorState(boolean inErrorState) {
        this.inErrorState = inErrorState;
    }

    public String getAuthToken() {
        return "Token token=" + appSessionHolder.get().get().getApiToken();
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

    public interface View extends Presenter.View {

        void load(String localizedUrl);

        void reload(String localizedUrl);

        void setRefreshing(boolean refreshing);

        void showError(int code);
    }
}
