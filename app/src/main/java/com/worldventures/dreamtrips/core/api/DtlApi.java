package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;

import retrofit.http.GET;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/api/dtl/cities")
    DtlLocationsHolder getDtlLocations(@Query("lat") double lat, @Query("lng") double lng, @Query("rad") int rad);
}
