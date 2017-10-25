package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class LocaleModule {

   @Provides
   @Singleton
   LocaleSwitcher provideLocaleSwitcher(Context context, SessionHolder sessionHolder) {
      return new LocaleSwitcher(context, sessionHolder);
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction provideLocaleSwitcherLogoutAction(LocaleSwitcher localeSwitcher) {
      return localeSwitcher::resetLocale;
   }
}
