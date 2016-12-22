package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;
import com.worldventures.dreamtrips.core.api.DateTimeSerializer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;
import com.worldventures.dreamtrips.core.utils.PersistentCookieStore;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.GsonAdaptersBucketBodyImpl;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.GsonAdaptersBucketCoverBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.GsonAdaptersBucketPostBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.GsonAdaptersBucketStatusBody;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.serializer.FeedEntityDeserializer;
import com.worldventures.dreamtrips.modules.feed.model.serializer.FeedItemDeserializer;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.model.serializer.SettingsDeserializer;
import com.worldventures.dreamtrips.modules.settings.model.serializer.SettingsSerializer;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
            .registerTypeAdapter(FeedItem.class, new FeedItemDeserializer())
            .registerTypeAdapter(FeedEntityHolder.class, new FeedEntityDeserializer())
            .registerTypeAdapter(Setting.class, new SettingsDeserializer())
            .registerTypeAdapter(Setting.class, new SettingsSerializer())
            //new
            .registerTypeAdapterFactory(new GsonAdaptersBucketPostBody())
            .registerTypeAdapterFactory(new GsonAdaptersBucketCoverBody())
            .registerTypeAdapterFactory(new GsonAdaptersBucketStatusBody())
            .registerTypeAdapterFactory(new GsonAdaptersBucketBodyImpl())
            .create();
   }

   @Provides
   CookieManager provideCookieManager(Context context) {
      return new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
   }

   @Provides
   OkHttpClient provideOkHttpClient(CookieManager cookieManager) {
      OkHttpClient okHttpClient = new OkHttpClient();
      okHttpClient.setCookieHandler(cookieManager);
      //Currently `api/{uid}/likes` (10k+ms)
      okHttpClient.setConnectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      okHttpClient.setReadTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      okHttpClient.setWriteTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      return okHttpClient;
   }

   @Provides
   HeaderProvider provideHeaderProvider(SessionHolder<UserSession> appSessionHolder, AppVersionNameBuilder appVersionNameBuilder) {
      return new HeaderProvider(appSessionHolder, appVersionNameBuilder);
   }
}
