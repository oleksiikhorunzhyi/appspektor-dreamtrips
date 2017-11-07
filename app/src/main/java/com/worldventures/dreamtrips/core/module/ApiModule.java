package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.DateTimeDeserializer;
import com.worldventures.core.utils.DateTimeSerializer;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;
import com.worldventures.dreamtrips.core.utils.PersistentCookieStore;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Date;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ApiModule {

   @Provides
   Gson provideGson() {
      return new GsonBuilder().serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory("unknown"))
            .registerTypeAdapter(Date.class, new DateTimeDeserializer())
            .registerTypeAdapter(Date.class, new DateTimeSerializer())
            .create();
   }

   @Provides
   CookieManager provideCookieManager(Context context) {
      return new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
   }

   @Provides
   HeaderProvider provideHeaderProvider(AppVersionNameBuilder appVersionNameBuilder) {
      return new HeaderProvider(appVersionNameBuilder);
   }
}
