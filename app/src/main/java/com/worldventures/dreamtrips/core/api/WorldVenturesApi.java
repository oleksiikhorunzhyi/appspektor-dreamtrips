package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface WorldVenturesApi {
    String DEFAULT_URL = "http://dtapp.worldventures.biz";

    @FormUrlEncoded
    @POST("/ipadapp/api/v1/auth/token")
    public void getToken(@Field("username") String username, @Field("password") String password, Callback<JsonObject> callback);

    /**
     * Currently only one country
     */
    @GET("http://sharedservices.worldventures.biz/LandingPageServices.svc/GetWebsiteDocumentsByCountry?dt=DTApp&cn=US&lc=EN\n")
    public void getWebSiteDocumentsByCountry(Callback<JsonObject> callback);

}
