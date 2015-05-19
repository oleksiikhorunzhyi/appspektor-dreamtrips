package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;

public class StaticPageProvider {

    private SessionHolder<UserSession> appSessionHolder;
    private final StaticPageHolder storage;

    public StaticPageProvider(SessionHolder<UserSession> appSessionHolder, StaticPageHolder storage) {
        this.appSessionHolder = appSessionHolder;
        this.storage = storage;
    }

    private AppConfig.URLS.Config getConfig() {
        AppConfig appConfig = appSessionHolder.get().get().getGlobalConfig();
        AppConfig.URLS urls = appConfig.getUrls();

        return BuildConfig.DEBUG ? urls.getQA() : urls.getProduction();
    }

    public String getStaticInfoUrl(String title) {
        if (storage.get().isPresent()) return storage.get().get().getUrlByTitle(title);
        else return "";
    }

    public String getEnrollUrl() {
        return getConfig().getEnrollMemeberURL(appSessionHolder.get().get().getUsername());
    }

    public String getEnrollRepUrl() {
        return getConfig().getEnrollRepURL(appSessionHolder.get().get().getUsername());
    }

    public String getoTAPageURL() {
        UserSession userSession = appSessionHolder.get().get();
        return getConfig().getoTAPageURL()
                .replace(AppConfig.USER_ID, userSession.getUser().getUsername())
                .replace(AppConfig.TOKEN, userSession.getLegacyApiToken());
    }

    public String getTrainingVideosURL() {
        return getConfig().getTrainingVideosURL();
    }
}
