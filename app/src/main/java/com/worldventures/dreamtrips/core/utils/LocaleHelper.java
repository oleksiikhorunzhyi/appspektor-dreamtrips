package com.worldventures.dreamtrips.core.utils;


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
        AvailableLocale mappedLocale = null;
        if (localesStorage.get().isPresent()) {
            ArrayList<AvailableLocale> availableLocales = localesStorage.get().get();
            mappedLocale = Queryable.from(availableLocales).firstOrDefault(l ->
                            l.getLocale().toLowerCase().equals(user.getLocale().toLowerCase())
            );
        }
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

}
