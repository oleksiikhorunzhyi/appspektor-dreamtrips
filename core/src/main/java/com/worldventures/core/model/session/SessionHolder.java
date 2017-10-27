package com.worldventures.core.model.session;

import com.crashlytics.android.Crashlytics;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.storage.complex_objects.ComplexObjectStorage;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.core.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.core.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SessionHolder extends ComplexObjectStorage<UserSession> {

   private final static String[] supportedLanguageCodes = new String[]{"en-us", "el-cy", "el-gr", "es-us", "hu-hu", "ms-my",
         "ro-hu", "sv-se", "zh", "zh-cn", "zh-hk", "zh-tw", "zh-sg", "zh-my"};
   private final List<Locale> supportedLocales;

   public SessionHolder(SimpleKeyValueStorage keyValueStorage) {
      super(keyValueStorage, "SESSION_KEY", UserSession.class);
      supportedLocales = new ArrayList<>(Queryable.from(supportedLanguageCodes)
            .map(LocaleHelper::buildFromLanguageCode).toList());
   }

   @Override
   public Optional<UserSession> get() {
      Optional<UserSession> sessionOptional = super.get();
      if (sessionOptional.isPresent()) {
         UserSession userSession = sessionOptional.get();
         if (userSession.user() == null) {
            Crashlytics.logException(new IllegalStateException("UserSession storage is broken"));
            destroy();
            sessionOptional = get();
         } else {
            sessionOptional = Optional.of(ImmutableUserSession.copyOf(userSession));
         }
      }
      return sessionOptional;
   }

   @Override
   public void put(UserSession userSession) {
      ImmutableUserSession temp = ImmutableUserSession.copyOf(userSession);
      if (Queryable.from(supportedLocales).firstOrDefault(locale ->
            LocaleHelper.compareLocales(LocaleHelper.buildFromLanguageCode(userSession.locale()), locale)) == null) {
         temp = temp.withLocale(Locale.US.getLanguage() + "-" + Locale.US.getCountry());
      }
      super.put(temp);
   }
}
