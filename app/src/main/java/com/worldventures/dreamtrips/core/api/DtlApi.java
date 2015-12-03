package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLead;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/api/dtl/v2/locations")
    ArrayList<DtlLocation> getNearbyDtlLocations(@Query("ll") String latLng, @Query("query") String query);

    @GET("/api/dtl/v2/locations/{id}/merchants")
    ArrayList<DTlMerchant> getDtlPlaces(@Path("id") String locationId);

    @FormUrlEncoded
    @POST("/api/dtl/v2/merchants/{id}/estimations")
    EstimationPointsHolder estimatePoints(@Path("id") String placeId,
                                          @Field("bill_total") double price,
                                          @Field("checkin_time") String checkinTime);

    @POST("/api/dtl/v2/merchants/{id}/transactions")
    DtlTransactionResult earnPoints(@Path("id") String placeId,
                                    @Body DtlTransaction.Request request);

    @FormUrlEncoded
    @POST("/api/dtl/v2/merchants/{id}/ratings")
    Void rate(@Path("id") String placeId, @Field("rating") int stars,
              @Field("transaction_id") String transactionId);

    @POST("/api/dtl/v2/leads")
    Void suggestPlace(@Body DtlLead lead);
}
