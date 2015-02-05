package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.BuildConfig;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface WorldVenturesApi {
    String DEFAULT_URL = BuildConfig.WorldVenturesApi;

    @FormUrlEncoded
    @POST("/ipadapp/api/v1/auth/token")
    public void getToken(@Field("username") String username, @Field("password") String password, Callback<JsonObject> callback);

    @FormUrlEncoded
    @POST("/ipadapp/api/v1/auth/token")
    public JsonObject getToken(@Field("username") String username, @Field("password") String password);
}
