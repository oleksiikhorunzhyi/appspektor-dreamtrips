package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.core.modules.settings.model.serializer.SettingsDeserializer;
import com.worldventures.core.modules.settings.model.serializer.SettingsSerializer;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.DateTimeDeserializer;
import com.worldventures.core.utils.DateTimeSerializer;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;
import com.worldventures.dreamtrips.core.utils.PersistentCookieStore;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketBodyImpl;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketCoverBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketPostBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketStatusBody;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.serializer.FeedEntityDeserializer;
import com.worldventures.dreamtrips.social.ui.feed.model.serializer.FeedItemDeserializer;

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
   HeaderProvider provideHeaderProvider(AppVersionNameBuilder appVersionNameBuilder) {
      return new HeaderProvider(appVersionNameBuilder);
   }
}
