package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;

import retrofit.http.GET;

public interface SharedServicesApi {

    @GET("/LandingPageServices.svc/GetVideos?poe=DTAPP&country=US")
    ArrayList<Video> getVideos();

    @GET("/LandingPageServices.svc/GetWebsiteDocumentsByCountry?dt=DTApp&cn=US&lc=EN")
    StaticPageConfig getStaticConfig();

}
