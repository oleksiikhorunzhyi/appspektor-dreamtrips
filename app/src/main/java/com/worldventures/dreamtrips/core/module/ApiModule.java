package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.core.modules.settings.model.serializer.SettingsDeserializer;
import com.worldventures.core.modules.settings.model.serializer.SettingsSerializer;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.DateTimeDeserializer;
import com.worldventures.core.utils.DateTimeSerializer;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;
import com.worldventures.dreamtrips.core.utils.PersistentCookieStore;
import com.worldventures.dreamtrips.modules.dtl.util.RuntimeTypeAdapterFactory;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.DtlTransactionPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.DtlTransactionListPath;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketBodyImpl;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketCoverBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketPostBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.GsonAdaptersBucketStatusBody;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.serializer.FeedEntityDeserializer;
import com.worldventures.dreamtrips.social.ui.feed.model.serializer.FeedItemDeserializer;

import org.immutables.gson.adapter.util.ImmutablesGsonAdaptersProvider;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ApiModule {

   @Provides
   Gson provideGson(Set<TypeAdapterFactory> typeAdapterFactories) {
      GsonBuilder builder = new GsonBuilder().serializeNulls()
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
            .registerTypeAdapterFactory(new GsonAdaptersBucketBodyImpl());


      for (TypeAdapterFactory factory : typeAdapterFactories) {
         builder.registerTypeAdapterFactory(factory);
      }
      return builder.create();
   }

   @Provides(type = Provides.Type.SET)
   TypeAdapterFactory provideDtlPathTypeAdapterFactory() {
      return RuntimeTypeAdapterFactory.of(MasterDetailPath.class)
            .registerSubtype(DtlFullscreenImagePath.class)
            .registerSubtype(DtlLocationsPath.class)
            .registerSubtype(DtlMerchantsPath.class)
            .registerSubtype(DtlCommentReviewPath.class)
            .registerSubtype(DtlLocationChangePath.class)
            .registerSubtype(DtlLocationsSearchPath.class)
            .registerSubtype(DtlStartPath.class)
            .registerSubtype(DtlDetailReviewPath.class)
            .registerSubtype(DtlMerchantDetailsPath.class)
            .registerSubtype(DtlMapPath.class)
            .registerSubtype(DtlTransactionListPath.class)
            .registerSubtype(DtlReviewsPath.class)
            .registerSubtype(DtlMapInfoPath.class)
            .registerSubtype(DtlTransactionPath.class);
   }

   @Provides(type = Provides.Type.SET_VALUES)
   Set<TypeAdapterFactory> provideImmutablesFactory() {
      Set<TypeAdapterFactory> set = new HashSet<>();
      set.addAll(new ImmutablesGsonAdaptersProvider().getAdapters());
      return set;
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
