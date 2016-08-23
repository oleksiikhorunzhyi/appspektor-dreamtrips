package com.worldventures.dreamtrips.core.utils;


import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Locale;

public class LocaleHelper {

   private SessionHolder<UserSession> appSessionHolder;

   public LocaleHelper(SessionHolder<UserSession> appSessionHolder) {
      this.appSessionHolder = appSessionHolder;
   }

   public Locale getDefaultLocale() {
      if (!appSessionHolder.get().isPresent() || appSessionHolder.get().get().getLocale() == null)
         return Locale.getDefault();

      UserSession userSession = appSessionHolder.get().get();
      String language = userSession.getLocale().split("-")[0];
      String country = userSession.getLocale().split("-")[1];

      return new Locale(language, country);
   }

   public String getDefaultLocaleFormatted() {
      if (appSessionHolder.get().isPresent() && appSessionHolder.get().get().getLocale() != null) {
         return appSessionHolder.get().get().getLocale();
      }

      Locale locale = Locale.getDefault();
      return android.text.TextUtils.join("-", new String[]{locale.getLanguage(), locale.getCountry()});
   }

   public boolean isOwnLanguage(@Nullable String languageCode) {
      if (!appSessionHolder.get().isPresent()) return false;

      String locale = appSessionHolder.get().get().getLocale();
      String userLanguageCode = TextUtils.isEmpty(locale) ? getDefaultLocaleFormatted() : locale;
      return userLanguageCode.equalsIgnoreCase(languageCode);
   }
}
