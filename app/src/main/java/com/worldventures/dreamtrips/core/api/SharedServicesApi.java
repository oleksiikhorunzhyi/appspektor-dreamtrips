package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.GET;

public interface SharedServicesApi {

    String DEFAULT_URL = "http://sharedservices.worldventures.biz";

    /**
     * Currently only one country
     */
    @GET("/LandingPageServices.svc/GetWebsiteDocumentsByCountry?dt=DTApp&cn=US&lc=EN")
    public void getWebSiteDocumentsByCountry(Callback<JsonObject> callback);

}
