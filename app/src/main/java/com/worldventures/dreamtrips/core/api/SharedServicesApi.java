package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;
import com.worldventures.dreamtrips.modules.infopages.model.Video;

import java.util.List;

import retrofit.http.GET;

public interface SharedServicesApi {

    @GET("/LandingPageServices.svc/GetVideos?poe=DTAPP&country=US")
    public List<Video> getVideos();

    @GET("/LandingPageServices.svc/GetWebsiteDocumentsByCountry?dt=DTApp&cn=US&lc=EN")
    public StaticPageConfig getStaticConfig();

}
