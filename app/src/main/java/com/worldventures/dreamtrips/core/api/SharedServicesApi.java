package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;

import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SharedServicesApi {

    @GET("/LandingPageServices.svc/GetWebsiteDocumentsByCountry")
    StaticPageConfig getStaticConfig(@Query("dt") String dt,
                                     @Query("cn") String country,
                                     @Query("lc") String language);
}
