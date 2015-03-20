package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.model.Video;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface SharedServicesApi {

    String DEFAULT_URL = BuildConfig.SharedServicesApi;

    /**
     * Currently only one country
     */
    @GET("/LandingPageServices.svc/GetWebsiteDocumentsByCountry?dt=DTApp&cn=US&lc=EN")
    public void getWebSiteDocumentsByCountry(Callback<JsonObject> callback);


    @GET("/LandingPageServices.svc/GetVideos?poe=DTAPP&country=US")
    public List<Video> getVideos();

}
