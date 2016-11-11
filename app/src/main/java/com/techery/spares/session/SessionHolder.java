package com.techery.spares.session;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

// generic here is for backward compatibility - to excape changing usage thoughout project
// TODO :: 11.11.16 remove unused generic and it's usage
public class SessionHolder<S> extends ComplexObjectStorage<UserSession> {

   private final String[] supportedLanguageCodes = new String[] {"en-us", "el-cy", "el-gr", "es-us", "hu-hu", "ms-my",
         "ro-hu", "sv-se", "zh", "zh-cn", "zh-hk", "zh-tw"};
   private final List<Locale> supportedLocales;

   @Inject public SessionHolder(SimpleKeyValueStorage keyValueStorage) {
      super(keyValueStorage, "SESSION_KEY", UserSession.class);
      supportedLocales = new ArrayList<>(Queryable.from(supportedLanguageCodes)
            .map(LocaleHelper::buildFromLanguageCode).toList());
   }

   @Override
   public void put(UserSession userSession) {
      final Locale localeFromSession = LocaleHelper.buildFromLanguageCode(userSession.getLocale());
      if (Queryable.from(supportedLocales).count(locale ->
            LocaleHelper.compareLocales(localeFromSession, locale)) < 1) {
         userSession.setLocale(Locale.US.getLanguage() + "-" + Locale.US.getCountry());
      }
      super.put(userSession);
   }

   public interface Events {
      class SessionDestroyed {}
   }
}
