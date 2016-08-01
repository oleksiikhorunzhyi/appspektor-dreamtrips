package com.worldventures.dreamtrips.core.utils;


import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.Locale;

public class LocaleHelper {

    private LocalesHolder localesStorage;
    private SessionHolder<UserSession> appSessionHolder;

    public LocaleHelper(LocalesHolder localesStorage, SessionHolder<UserSession> appSessionHolder) {
        this.localesStorage = localesStorage;
        this.appSessionHolder = appSessionHolder;
    }

    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    /**
     * @return `Accept-Language` style formatted, e.g. `en-us`
     */
    public String getDefaultLocaleFormatted() {
        Locale locale = getDefaultLocale();
        return android.text.TextUtils.join("-", new String[]{locale.getLanguage(), locale.getCountry()});
    }

    public String getAccountLocaleFormatted(User user) {
        Locale userLocale = getAccountLocale(user);
        Locale locale = userLocale == null ? getDefaultLocale() : userLocale;
        return android.text.TextUtils.join("-", new String[]{locale.getLanguage(), locale.getCountry()})
                .toLowerCase();
    }

    public Locale getAccountLocale(User user) {
        if (user.getLocale() == null) return Locale.US;

        AvailableLocale mappedLocale = obtainAvailableLocale(user.getLocale());
        if (mappedLocale == null) {
            return Locale.US;
        } else {
            String language = mappedLocale.getLanguage();
            String country = mappedLocale.getCountry();
            return new Locale(language, country);
        }
    }

    public String getOwnAccountLocaleFormatted() {
        if (!appSessionHolder.get().isPresent()) return null;
        //
        User user = appSessionHolder.get().get().getUser();
        return getAccountLocaleFormatted(user);
    }

    public boolean isOwnLanguage(@Nullable String languageCode) {
        if (!appSessionHolder.get().isPresent()) return false;

        String userLanguageCode = appSessionHolder.get().get().getUser().getLocale().split("-")[0];
        return userLanguageCode.equalsIgnoreCase(languageCode);
    }

    private AvailableLocale obtainAvailableLocale(String localeName){
        AvailableLocale mappedLocale = null;
        if (localesStorage.get().isPresent()) {
            ArrayList<AvailableLocale> availableLocales = localesStorage.get().get();
            mappedLocale = Queryable.from(availableLocales).firstOrDefault(l ->
                    l.getLocale().toLowerCase().equals(localeName.toLowerCase())
            );
        }
        return mappedLocale;
    }
}
