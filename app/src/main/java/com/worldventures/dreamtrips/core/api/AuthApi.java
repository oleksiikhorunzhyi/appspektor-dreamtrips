package com.worldventures.dreamtrips.core.api;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface AuthApi {
    String DEFAULT_URL = "http://dreamTripsApi.dreamtrips.com/AuthenticationService/AuthenticationService.svc";

    @FormUrlEncoded
    @Headers("\"Content-Type\":\"application/json\"")
    @POST("/AuthenticateByUserName")
    public void authenticateByUserName(@Body String body, Callback<Object> callback);

}
