package com.worldventures.dreamtrips.api.http.provider;

import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.dreamtrips.api.facility.Log;
import com.worldventures.dreamtrips.api.facility.LoggingWrapper;
import com.worldventures.dreamtrips.api.http.EnvParams;
import com.worldventures.dreamtrips.api.http.service.DreamTripsBaseService;
import com.worldventures.dreamtrips.api.http.util.CurlInterceptor;

import java.util.concurrent.TimeUnit;

import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.HttpActionService;
import io.techery.janet.converter.Converter;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;
import io.techery.janet.okhttp3.OkClient;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class SimpleHttpServiceProvider implements HttpServiceProvider<ActionServiceWrapper> {

    private EnvParams env;

    public SimpleHttpServiceProvider(EnvParams envParams) {
        this.env = envParams;
    }

    @Override
    public ActionServiceWrapper provide() {
        // http client
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS);
        builder.networkInterceptors().add(chain -> {
            Request request = chain.request();
            Headers headers = request.headers().newBuilder().set("Accept-Encoding", "identity").build();
            Request newRequest = request.newBuilder().headers(headers).build();
            return chain.proceed(newRequest);
        });
        builder.interceptors().add(new CurlInterceptor(command ->
                Log.logCurl(command)
        ));
        builder.interceptors().add(new HttpLoggingInterceptor(message ->
                Log.logHttp(message)
        ).setLevel(HttpLoggingInterceptor.Level.BODY));
        HttpClient httpClient = new OkClient(builder.build());
        // converter
        Converter converter = new GsonConverter(new GsonProvider().provideGson());
        // service
        HttpActionService httpActionService = new HttpActionService(env.apiUrl(), httpClient, converter);
        DreamTripsBaseService dreamTripsBaseService = new DreamTripsBaseService(httpActionService, env);
        return new LoggingWrapper(dreamTripsBaseService);
    }
}
