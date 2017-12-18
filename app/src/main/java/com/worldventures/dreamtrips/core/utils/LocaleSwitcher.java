package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.utils.LocaleHelper;

import java.util.Locale;

public class LocaleSwitcher {

   private final Context context;
   private final SessionHolder sessionHolder;

   private Locale defaultSystemLocale;

   public LocaleSwitcher(Context context, SessionHolder sessionHolder) {
      this.context = context;
      this.sessionHolder = sessionHolder;
      this.defaultSystemLocale = Locale.getDefault();
   }

   public void onConfigurationLocaleChanged(Locale newLocal) {
      defaultSystemLocale = newLocal;
   }

   public void applyLocaleFromSession() {
      Locale localeToApply;

      if (sessionHolder.get().isPresent() && !TextUtils.isEmpty(sessionHolder.get().get().locale())) {
         localeToApply = LocaleHelper.buildFromLanguageCode(sessionHolder.get().get().locale());
      } else {
         localeToApply = defaultSystemLocale;
      }

      applyLocale(localeToApply);
   }

   private void applyLocale(Locale locale) {
      Locale.setDefault(locale);
      Resources resources = context.getResources();

      Configuration configuration = resources.getConfiguration();
      configuration.locale = locale;

      resources.updateConfiguration(configuration, resources.getDisplayMetrics());
   }

   public void resetLocale() {
      applyLocale(defaultSystemLocale);
   }
}
