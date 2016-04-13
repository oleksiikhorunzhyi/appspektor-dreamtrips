package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;

import java.net.CookieManager;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.CommandActionService;
import io.techery.janet.HttpActionService;
import io.techery.janet.Janet;
import io.techery.janet.http.HttpClient;
import timber.log.Timber;

@Module(
        includes = {JanetCommandModule.class, JanetServiceModule.class},
        complete = false, library = true)
public class JanetModule {
    public static final String JANET_QUALIFIER = "JANET";
    @Singleton
    @Provides(type = Provides.Type.SET)
    ActionService provideCommandService(@ForApplication Context context) {
        return new DaggerCommandServiceWrapper(new CommandActionService(), context);
    }

    @Singleton
    @Provides
    Janet provideJanet(Set<ActionService> services) {
        Janet.Builder builder = new Janet.Builder();
        for (ActionService service : services) {
            builder.addService(new TimberServiceWrapper(service));
        }
        return builder.build();
    }

    @Named(JANET_QUALIFIER)
    @Provides
    OkHttpClient provideJanetOkHttpClient(CookieManager cookieManager, @Named(JANET_QUALIFIER) Interceptor interceptor) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCookieHandler(cookieManager);
        okHttpClient.interceptors().add(interceptor);
        //Currently `api/{uid}/likes` (10k+ms)
        okHttpClient.setConnectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
        return okHttpClient;
    }

    @Named(JANET_QUALIFIER)
    @Provides
    Interceptor interceptor() {
        return chain -> {
            Request request = chain.request();
            Timber.d("---->");
            Timber.tag("OkHttpClient").d(request.headers().toString());
            Timber.tag("OkHttpClient").d(request.toString());
            Timber.d("---->");
            Timber.d("<----");
            Response response = chain.proceed(request);
            Timber.tag("OkHttpClient").d(response.toString());
            Timber.d("<----");
            return response;
        };
    }

    @Singleton
    @Provides
    HttpClient provideJanetHttpClient(@Named(JANET_QUALIFIER) OkHttpClient okHttpClient) {
        return new io.techery.janet.okhttp.OkClient(okHttpClient);
    }

    @Provides(type = Provides.Type.SET)
    ActionService provideHttpService(@ForApplication Context appContext, SessionHolder<UserSession> appSessionHolder,  HttpClient httpClient, Gson gson) {
        if (!appSessionHolder.get().isPresent()) return null;
        AppConfig appConfig = appSessionHolder.get().get().getGlobalConfig();
        if (appConfig == null) return null;
        AppConfig.URLS urls = appConfig.getUrls();
        String uploaderyApi = urls.getProduction().getUploaderyBaseURL();
        if (uploaderyApi == null) return null;
        return new AuthHttpServiceWrapper(new HttpActionService(uploaderyApi, httpClient, new io.techery.janet.gson.GsonConverter(gson)), appContext );
    }
}
