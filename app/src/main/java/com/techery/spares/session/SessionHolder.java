package com.techery.spares.session;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SessionHolder extends ComplexObjectStorage<UserSession> {

   private final static String[] supportedLanguageCodes = new String[]{"en-us", "el-cy", "el-gr", "es-us", "hu-hu", "ms-my",
         "ro-hu", "sv-se", "zh", "zh-cn", "zh-hk", "zh-tw", "zh-sg"};
   private final List<Locale> supportedLocales;

   public SessionHolder(SimpleKeyValueStorage keyValueStorage) {
      super(keyValueStorage, "SESSION_KEY", UserSession.class);
      supportedLocales = new ArrayList<>(Queryable.from(supportedLanguageCodes)
            .map(LocaleHelper::buildFromLanguageCode).toList());
   }

   @Override
   public void put(UserSession userSession) {
      final Locale localeFromSession = LocaleHelper.buildFromLanguageCode(userSession.getLocale());
      if (Queryable.from(supportedLocales).firstOrDefault(locale ->
            LocaleHelper.compareLocales(localeFromSession, locale)) == null) {
         userSession.setLocale(Locale.US.getLanguage() + "-" + Locale.US.getCountry());
      }
      super.put(userSession);
   }
}
