package com.worldventures.dreamtrips.core.utils;


import android.support.annotation.Nullable;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.Locale;

public class LocaleHelper {

    private LocalesHolder localesStorage;

    public LocaleHelper(LocalesHolder localesStorage) {
        this.localesStorage = localesStorage;
    }

    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    public boolean isTheSameLanguage(String locale1, String locale2){
        String language1 = obtainLanguageCode(locale1);
        String language2 = obtainLanguageCode(locale2);

        return TextUtils.equals(language1, language2);
    }

    /**
     * @return `Accept-Language` style formatted, e.g. `en-us`
     */
    public String getDefaultLocaleFormatted() {
        Locale locale = getDefaultLocale();
        return android.text.TextUtils.join("-", new String[]{locale.getLanguage(), locale.getCountry()});
    }

    public Locale getAccountLocale(User user) {
        if (user.getLocale() == null) return null;
        //
        // check mapped locale first
        AvailableLocale mappedLocale = obtainAvailableLocale(user.getLocale());

        String language;
        String country;
        if (mappedLocale == null) {
            String[] args = user.getLocale().split("-"); // e.g. en-us
            language = args[0];
            country = args[1];
        } else {
            language = mappedLocale.getLanguage();
            country = mappedLocale.getCountry();
        }

        return new Locale(language, country);
    }

    public String obtainLanguageCode(@Nullable String localeName) {
        if (localeName == null) return "en";

        AvailableLocale mappedLocale = obtainAvailableLocale(localeName);

        return mappedLocale == null ?
                localeName.split("-")[0] : mappedLocale.getLanguage();
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
