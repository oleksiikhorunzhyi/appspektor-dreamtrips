package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleSwitcher {

   private Context context;
   private Locale defaultSystemLocale;
   private Locale currentLocale;

   public LocaleSwitcher(Context context) {
      this.context = context;
      this.defaultSystemLocale = Locale.getDefault();
   }

   public void onConfigurationLocaleChanged(Locale newLocal) {
      defaultSystemLocale = newLocal;
   }

   public void applyLocale(Locale locale) {
      currentLocale = locale;
      Locale.setDefault(currentLocale);
      Resources resources = context.getResources();

      Configuration configuration = resources.getConfiguration();
      configuration.locale = currentLocale;

      resources.updateConfiguration(configuration, resources.getDisplayMetrics());
   }

   public void resetLocale() {
      applyLocale(defaultSystemLocale);
   }
}
