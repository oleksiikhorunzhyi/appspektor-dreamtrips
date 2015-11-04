package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleManager {

    private Context context;
    private Locale defaultSystemLocale;
    private Locale currentLocale;

    public LocaleManager(Context context) {
        this.context = context;
        this.defaultSystemLocale = Locale.getDefault();
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
        Locale.setDefault(currentLocale);
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = currentLocale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public void resetLocale() {
        setLocale(defaultSystemLocale);
    }

    public void localeChanged(Locale newLocal){
        defaultSystemLocale = newLocal;
    }
}
