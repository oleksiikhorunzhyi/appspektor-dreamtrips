package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.text.TextUtils;

import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;

import java.util.ArrayList;
import java.util.Locale;

public class LocaleUtils {
    private LocaleUtils() {
    }

    public static String getAcceptLanguage(Context context) {
        Locale locale = getLocale(context);
        return getAcceptLanguage(locale);
    }

    public static String substituteActualLocale(Context context, String url,
                                                ComplexObjectStorage<ArrayList<AvailableLocale>> localseStorage) {
        Locale locale = getLocale(context);

        return url
                .replaceAll("\\{locale\\}", getAcceptLanguage(locale))
                .replaceAll("\\{language\\}", locale.getLanguage())
                .replaceAll("\\{country\\}", locale.getCountry());
    }

    private static String getAcceptLanguage(Locale locale) {
        return TextUtils.join("-", new String[]{locale.getLanguage(), locale.getCountry()});
    }

    private static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

}
