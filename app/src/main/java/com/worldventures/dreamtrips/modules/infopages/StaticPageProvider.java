package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;

public class StaticPageProvider {

    private StaticPageHolder storage;
    private SessionHolder<UserSession> appSessionHolder;
    private LocaleHelper localeHelper;

    public StaticPageProvider(StaticPageHolder storage, SessionHolder<UserSession> appSessionHolder, LocaleHelper localeHelper) {
        this.storage = storage;
        this.appSessionHolder = appSessionHolder;
        this.localeHelper = localeHelper;
    }

    private AppConfig.URLS.Config getConfig() {
        AppConfig appConfig = appSessionHolder.get().get().getGlobalConfig();
        AppConfig.URLS urls = appConfig.getUrls();

        return urls.getProduction();
    }

    public String getStaticInfoUrl(String title) {
        if (storage.get().isPresent()) return storage.get().get().getUrlByTitle(title);
        else return "";
    }

    public String getEnrollMemberUrl() {
        String enrollUrlFromServer = getConfig().getEnrollMemberURL(appSessionHolder.get().get().getUsername());
        String additionalParams = "utm_medium=MobileApp&utm_source=MobileApp&utm_campaign=MobileApp";
        return enrollUrlFromServer + "&" + additionalParams;
    }

    public String getEnrollRepUrl() {
        return getConfig().getEnrollRepURL(appSessionHolder.get().get().getUsername());
    }

    public String getEnrollRestaurantUrl() {
        return BuildConfig.DTL_ENROLL_RESTAURANT_URL;
    }

    public String getOtaPageURL() {
        UserSession userSession = appSessionHolder.get().get();
        return getConfig().getOtaPageURL()
                .replace(AppConfig.USER_ID, userSession.getUser().getUsername())
                .replace(AppConfig.TOKEN, userSession.getLegacyApiToken())
                .replace(AppConfig.LOCALE, localeHelper.getDefaultLocaleFormatted());
    }

    public String getTrainingVideosURL() {
        return getConfig().getTrainingVideosURL();
    }
}
