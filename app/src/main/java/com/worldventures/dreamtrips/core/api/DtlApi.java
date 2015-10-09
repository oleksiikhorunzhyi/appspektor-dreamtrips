package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlacesHolder;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/api/dtl/cities")
    DtlLocationsHolder getDtlLocations(@Query("lat") double lat, @Query("lng") double lng, @Query("rad") int rad);

    @GET("/api/dtl/cities/{id}/places")
    DtlPlacesHolder getDtlPlaces(@Path("id") int locationId);
}
