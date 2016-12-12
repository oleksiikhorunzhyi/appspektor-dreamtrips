package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Locale;

public class LocaleSwitcher {

   private final Context context;
   private final SessionHolder<UserSession> sessionHolder;

   private Locale defaultSystemLocale;
   private Locale currentLocale;

   public LocaleSwitcher(Context context, SessionHolder<UserSession> sessionHolder) {
      this.context = context;
      this.sessionHolder = sessionHolder;
      this.defaultSystemLocale = Locale.getDefault();
   }

   public void onConfigurationLocaleChanged(Locale newLocal) {
      defaultSystemLocale = newLocal;
   }

   public void applyLocaleFromSession() {
      Locale localeToApply;

      if (sessionHolder.get().isPresent() && !TextUtils.isEmpty(sessionHolder.get().get().getLocale())) {
         localeToApply = LocaleHelper.buildFromLanguageCode(sessionHolder.get().get().getLocale());
      } else {
         localeToApply = defaultSystemLocale;
      }

      applyLocale(localeToApply);
   }

   private void applyLocale(Locale locale) {
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
