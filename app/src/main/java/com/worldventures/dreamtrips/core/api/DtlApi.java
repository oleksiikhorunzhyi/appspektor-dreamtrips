package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlacesHolder;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestMerchantPostData;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/api/dtl/cities")
    DtlLocationsHolder getDtlLocations(@Query("lat") double lat, @Query("lng") double lng);

    @GET("/api/dtl/cities/{id}/places")
    DtlPlacesHolder getDtlPlaces(@Path("id") int locationId);

    @GET("/api/dtl/places/{id}/points")
    EstimationPointsHolder getDtlPlacePointsEstimation(@Path("id") int id, @Query("price") double price);

    @POST("/api/dtl/places/{id}/points")
    DtlTransactionResult earnPoints(@Path("id") int locationId, @Body DtlTransaction request);

    @POST("/api/dtl/places/{id}/suggestion")
    Void suggestDining(@Path("id") int placeId, @Body SuggestMerchantPostData request);

    @POST("/api/dtl/places/{id}/rating")
    Void rate(@Path("id") int locationId, @Query("stars") int stars);
}
