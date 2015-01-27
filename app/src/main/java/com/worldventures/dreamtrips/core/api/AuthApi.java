package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.BuildConfig;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface AuthApi {
    String DEFAULT_URL = BuildConfig.AuthApiUrl;

    @Headers("\"Content-Type\":\"application/json\"")
    @POST("/AuthenticateByUserName")
    public void authenticateByUserName(@Body String body, Callback<Object> callback);

}
