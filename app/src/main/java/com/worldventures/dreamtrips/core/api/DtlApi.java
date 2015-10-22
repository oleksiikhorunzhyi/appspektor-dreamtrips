package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestPlacePostData;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/locations")
    DtlLocationsHolder getDtlLocations(@Query("lat") double lat, @Query("lng") double lng);

    @GET("/locations/{id}/merchants")
    ArrayList<DtlPlace> getDtlPlaces(@Path("id") String locationId);

    @GET("/merchants/{id}/points")
    EstimationPointsHolder getDtlPlacePointsEstimation(@Path("id") String placeId,
                                                       @Query("price") double price);

    @POST("/merchants/{id}/points")
    DtlTransactionResult earnPoints(@Path("id") String placeId,
                                    @Body DtlTransaction request);

    @POST("/merchants/{id}/suggestion")
    Void suggestDining(@Path("id") String placeId, @Body SuggestPlacePostData request);

    @POST("/merchants/{id}/rating")
    Void rate(@Path("id") String placeId, @Query("stars") int stars);

    @POST("/merchants/")
    Void suggestPlace(@Body SuggestPlacePostData request);
}
