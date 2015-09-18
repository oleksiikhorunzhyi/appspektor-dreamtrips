package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innahema.collections.query.queriables.Queryable;
import com.squareup.okhttp.OkHttpClient;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.ConfigApi;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;
import com.worldventures.dreamtrips.core.api.DateTimeSerializer;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.InterceptingOkClient;
import com.worldventures.dreamtrips.core.utils.LocaleUtils;
import com.worldventures.dreamtrips.core.utils.PersistentCookieStore;
import com.worldventures.dreamtrips.modules.common.event.NotificationsCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.serializer.FeedModelDeserializer;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(complete = false, library = true)
public class ApiModule {

    @Provides
    DreamTripsApi provideApi(RestAdapter adapter) {
        return adapter.create(DreamTripsApi.class);
    }

    @Provides
    RestAdapter provideRestAdapter(GsonConverter gsonConverter, RequestInterceptor requestInterceptor, OkClient okClient) {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.DreamTripsApi)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(gsonConverter)
                .setClient(okClient)
                .setRequestInterceptor(requestInterceptor)
                .build();
    }

    @Provides
    RequestInterceptor provideRequestInterceptor(Context context, SessionHolder<UserSession> appSessionHolder) {
        return request -> {
            if (appSessionHolder.get().isPresent()) {
                UserSession userSession = appSessionHolder.get().get();
                String authToken = "Token token=" + userSession.getApiToken();
                request.addHeader("Authorization", authToken);
            }
            request.addHeader("Accept-Language", LocaleUtils.getAcceptLanguage(context));
            request.addHeader("Accept", "application/com.dreamtrips.api+json;version="
                    + BuildConfig.API_VERSION);
        };
    }

    @Provides
    GsonConverter provideGsonConverter(Gson gson) {
        return new GsonConverter(gson);
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .registerTypeAdapter(BaseEventModel.class, new FeedModelDeserializer())
                .create();
    }

    @Provides
    @Singleton
    ConfigApi provideS3Api(GsonConverter gsonConverter) {
        return createRestAdapter(BuildConfig.S3Api, gsonConverter).create(ConfigApi.class);
    }

    private RestAdapter createRestAdapter(String endpoint, GsonConverter gsonConverter) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(gsonConverter)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    @Provides
    @Singleton
    SharedServicesApi provideSharedServicesApi(SessionHolder<UserSession> session, GsonConverter gsonConverter) {
        String baseUrl = BuildConfig.SharedServicesApi;

        Optional<UserSession> userSessionOptional = session.get();

        if (userSessionOptional.isPresent()) {
            AppConfig config = session.get().get().getGlobalConfig();

            if (config != null) {
                baseUrl = config.getUrls().getProduction().getAuthBaseURL();
            }
        }

        return createRestAdapter(baseUrl, gsonConverter).create(SharedServicesApi.class);
    }

    @Provides
    OkClient provideOkClient(OkHttpClient okHttpClient, SnappyRepository db, @Global EventBus eventBus) {
        InterceptingOkClient interceptingOkClient = new InterceptingOkClient(okHttpClient);
        interceptingOkClient.setResponseHeaderListener(headers -> {
            Header header = Queryable.from(headers).firstOrDefault(element -> "Unread-Notifications-Count".equals(element.getName()));
            if (header != null) {
                int notificationsCount;
                try {
                    notificationsCount = Integer.parseInt(header.getValue());
                } catch (Exception e) {
                    notificationsCount = 0;
                }
                db.saveNotificationsCount(notificationsCount);
                eventBus.post(new NotificationsCountChangedEvent());
            }

        });
        return interceptingOkClient;
    }

    @Provides
    OkHttpClient provideOkHttpClient(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient();
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
        okHttpClient.setCookieHandler(cookieManager);
        return okHttpClient;
    }


}