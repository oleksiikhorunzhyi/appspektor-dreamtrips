package com.worldventures.dreamtrips.core.api;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface AuthApi {
    String DEFAULT_URL = "https://dreamTripsApi.dreamtrips.com/AuthenticationService/AuthenticationService.svc";

    @Headers("\"Content-Type\":\"application/json\"")
    @POST("/AuthenticateByUserName")
    public void authenticateByUserName(@Body String body, Callback<Object> callback);

}
