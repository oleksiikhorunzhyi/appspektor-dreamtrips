package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.BuildConfig;

import retrofit.Callback;
import retrofit.http.GET;

public interface SharedServicesApi {

    String DEFAULT_URL = BuildConfig.SharedServicesApi;

    /**
     * Currently only one country
     */
    @GET("/LandingPageServices.svc/GetWebsiteDocumentsByCountry?dt=DTApp&cn=US&lc=EN")
    public void getWebSiteDocumentsByCountry(Callback<JsonObject> callback);

}
