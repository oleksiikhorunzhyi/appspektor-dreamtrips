package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.ContactTime;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlacesHolder;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.RateContainer;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/api/dtl/cities")
    DtlLocationsHolder getDtlLocations(@Query("lat") double lat, @Query("lng") double lng, @Query("rad") int rad);

    @GET("/api/dtl/cities/{id}/places")
    DtlPlacesHolder getDtlPlaces(@Path("id") int locationId);

    @GET("/api/dtl/places/{id}/points")
    EstimationPointsHolder getDtlPlacePointsEstimation(@Path("id") int id, @Query("price") double price);

    @POST("/api/dtl/places/{id}/points")
    DtlTransactionResult earnPoints(@Path("id") int locationId, @Body DtlTransaction request);

    @POST("/api/dtl/places/{id}/suggestion")
    Void suggestDining(@Path("id") int placeId, @Query("contact_name") String contactName,
                       @Query("phone") String phone, @Query("contact_time") ContactTime contactTime,
                       @Query("rate") RateContainer rateContainer, @Query("description") String desc);
}
