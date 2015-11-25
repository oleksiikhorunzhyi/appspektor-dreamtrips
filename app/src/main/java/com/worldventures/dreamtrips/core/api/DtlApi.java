package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestPlacePostData;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/api/dtl/v2/locations/search")
    ArrayList<DtlLocation> searchDtlLocations(@Query("text") String keyword);

    @GET("/api/dtl/v2/locations")
    ArrayList<DtlLocation> getNearbyDtlLocations(@Query("lat") double lat, @Query("lng") double lng);

    @GET("/api/dtl/v2/locations/{id}/merchants")
    ArrayList<DtlPlace> getDtlPlaces(@Path("id") String locationId);

    @GET("/api/dtl/v2/merchants/{id}/points")
    EstimationPointsHolder getDtlPlacePointsEstimation(@Path("id") String placeId,
                                                       @Query("billTotal") double price,
                                                       @Query("checkinTimestamp") String checkinTime);

    @POST("/api/dtl/v2/merchants/{id}/points")
    DtlTransactionResult earnPoints(@Path("id") String placeId,
                                    @Body DtlTransaction request);

    @POST("/api/dtl/v2/merchants/{id}/suggestion")
    Void suggestDining(@Path("id") String placeId, @Body SuggestPlacePostData request);

    @FormUrlEncoded
    @POST("/api/dtl/v2/merchants/{id}/rating")
    Void rate(@Path("id") String placeId, @Field("stars") int stars, @Field("transactionId") String transactionId);

    @POST("/api/dtl/v2/merchants/")
    Void suggestPlace(@Body SuggestPlacePostData request);
}
