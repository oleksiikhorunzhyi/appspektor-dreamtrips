package com.worldventures.core.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.worldventures.core.model.session.SessionHolder;

import java.util.Locale;

public final class LocaleHelper {

   private LocaleHelper() {
   }

   public static String getDefaultLocaleFormatted() {
      return formatLocale(LocaleHelper.getDefaultLocale());
   }

   /**
    * Sugar to wrap <code>Locale.getDefault()</code> to have all locale accessing code in one place
    *
    * @return Locale that is currently set as default
    */
   public static Locale getDefaultLocale() {
      return Locale.getDefault();
   }

   public static String formatLocale(Locale locale) {
      return TextUtils.join("-", new String[]{locale.getLanguage(), locale.getCountry()});
   }

   public static boolean isOwnLanguage(SessionHolder sessionHolder, @Nullable String languageCode) {
      if (!sessionHolder.get().isPresent()) { return false; }

      String locale = sessionHolder.get().get().locale();
      String userLanguageCode = TextUtils.isEmpty(locale) ? getDefaultLocaleFormatted() : locale;
      return userLanguageCode.split("-")[0].equalsIgnoreCase(languageCode);
   }

   public static Locale buildFromLanguageCode(String languageCode) {
      final String[] codeParts = languageCode.split("-");

      if (codeParts.length == 1) { return new Locale(codeParts[0]); } else {
         return new Locale(codeParts[0], codeParts[1]);
      }
   }

   public static boolean compareLocales(Locale lhs, Locale rhs) {
      return lhs.getCountry().equalsIgnoreCase(rhs.getCountry())
            && lhs.getLanguage().equalsIgnoreCase(rhs.getLanguage());
   }

   public static String obtainLanguageCode(String localName) {
      return localName.split("-")[0];
   }

}
