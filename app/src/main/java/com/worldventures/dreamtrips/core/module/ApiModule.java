package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;
import com.worldventures.dreamtrips.core.utils.PersistentCookieStore;

import java.net.CookieManager;
import java.net.CookiePolicy;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ApiModule {

   @Provides
   CookieManager provideCookieManager(Context context) {
      return new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
   }

   @Provides
   HeaderProvider provideHeaderProvider(AppVersionNameBuilder appVersionNameBuilder) {
      return new HeaderProvider(appVersionNameBuilder);
   }
}
