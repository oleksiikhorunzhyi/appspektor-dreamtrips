package com.worldventures.dreamtrips.modules.infopages.presenter;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.WebViewReloadEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;


public class WebViewFragmentPresenter<T extends WebViewFragmentPresenter.View> extends Presenter<T> {

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

    public void onEvent(WebViewReloadEvent event) {
        if (view instanceof StaticInfoFragment.TrainingVideosFragment) {
            view.reload();
        }
    }

    public String getEnrollUrl() {
        return setLocale(getConfig().getEnrollMemeberURL(appSessionHolder.get().get().getUsername()));
    }

    public String getEnrollRepUrl() {
        return setLocale(getConfig().getEnrollRepURL(appSessionHolder.get().get().getUsername()));
    }

    public String setLocale(String url) {
        return url.replace("{locale}", context.getResources().getConfiguration().locale.getLanguage());
    }

    public String getStaticInfoUrl(String title) {
        StaticPageConfig staticPageConfig = appSessionHolder.get().get().getStaticPageConfig();
        if (staticPageConfig != null) return staticPageConfig.getUrlByTitle(title);
        else return "";
    }

    public AppConfig.URLS.Config getConfig() {
        AppConfig appConfig = appSessionHolder.get().get().getGlobalConfig();
        AppConfig.URLS urls = appConfig.getUrls();

        return BuildConfig.DEBUG ? urls.getProduction() : urls.getQA();
    }


    public UserSession getCurrentUser() {
        return appSessionHolder.get().get();
    }

    public interface View extends Presenter.View {
        void reload();
    }

}
