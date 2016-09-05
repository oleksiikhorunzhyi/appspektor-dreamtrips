package com.worldventures.dreamtrips.core.module;

import android.content.Context;
import android.os.Build;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innahema.collections.query.queriables.Queryable;
import com.squareup.okhttp.OkHttpClient;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;
import com.worldventures.dreamtrips.core.api.DateTimeSerializer;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.error.DTErrorHandler;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.InterceptingOkClient;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
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
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.serializer.MapObjectDeserializer;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import static com.worldventures.dreamtrips.core.utils.InterceptingOkClient.ResponseHeaderListener;

@Module(complete = false, library = true)
public class ApiModule {

   @Provides
   DreamTripsApi provideApi(RestAdapter adapter) {
      return adapter.create(DreamTripsApi.class);
   }

   @Provides
   RestAdapter provideRestAdapter(GsonConverter gsonConverter, RequestInterceptor requestInterceptor, OkClient okClient) {
      return new RestAdapter.Builder().setEndpoint(BuildConfig.DreamTripsApi)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setConverter(gsonConverter)
            .setClient(okClient)
            .setRequestInterceptor(requestInterceptor)
            .build();
   }

   @Provides
   RestAdapter.Builder provideRestAdapterBuilder(GsonConverter gsonConverter, RequestInterceptor requestInterceptor, OkClient okClient) {
      return new RestAdapter.Builder().setEndpoint(BuildConfig.DreamTripsApi)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setConverter(gsonConverter)
            .setClient(okClient)
            .setRequestInterceptor(requestInterceptor);
   }


   @Provides
   RequestInterceptor provideRequestInterceptor(SessionHolder<UserSession> appSessionHolder, LocaleHelper localeHelper, AppVersionNameBuilder appVersionNameBuilder) {
      return request -> {
         if (appSessionHolder.get().isPresent()) {
            UserSession userSession = appSessionHolder.get().get();
            String authToken = "Token token=" + userSession.getApiToken();
            request.addHeader("Authorization", authToken);
         }
         request.addHeader("Accept-Language", localeHelper.getDefaultLocaleFormatted());
         request.addHeader("Accept", "application/com.dreamtrips.api+json;version=" + BuildConfig.API_VERSION);

         request.addHeader("DT-App-Version", appVersionNameBuilder.getSemanticVersionName());
         request.addHeader("DT-App-Platform", String.format("android-%d", Build.VERSION.SDK_INT));
      };
   }

   @Provides
   GsonConverter provideGsonConverter(Gson gson) {
      return new GsonConverter(gson);
   }

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
            //
            .registerTypeAdapter(MapObjectHolder.class, new MapObjectDeserializer<>())
            .create();
   }

   private RestAdapter createRestAdapter(String endpoint, GsonConverter gsonConverter) {
      return new RestAdapter.Builder().setEndpoint(endpoint)
            .setConverter(gsonConverter)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();
   }

   @Provides
   OkClient provideOkClient(OkHttpClient okHttpClient, Set<ResponseHeaderListener> listeners) {
      InterceptingOkClient interceptingOkClient = new InterceptingOkClient(okHttpClient);
      interceptingOkClient.setResponseHeaderListener(headers -> {
         Queryable.from(listeners).forEachR(arg -> arg.onResponse(headers));
      });
      return interceptingOkClient;
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
   DTErrorHandler providesErrorHandler(Context context) {
      return new DTErrorHandler(context);
   }
}
