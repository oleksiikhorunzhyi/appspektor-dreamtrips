package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DtlApi {

    @GET("/api/dtl/v2/locations")
    List<DtlExternalLocation> getNearbyLocations(@Query("ll") String latLng);

    @GET("/api/dtl/v2/locations")
    List<DtlExternalLocation> searchLocations(@Query("query") String query);

    @GET("/api/dtl/v2/merchants")
    List<DtlMerchant> getNearbyDtlMerchants(@Query("ll") String ll);

    @FormUrlEncoded
    @POST("/api/dtl/v2/merchants/{id}/estimations")
    EstimationPointsHolder estimatePoints(@Path("id") String merchantId,
                                          @Field("bill_total") double price,
                                          @Field("currency_code") String currencyCode,
                                          @Field("checkin_time") String checkinTime);

    @POST("/api/dtl/v2/merchants/{id}/transactions")
    DtlTransactionResult earnPoints(@Path("id") String merchantId,
                                    @Body DtlTransaction.Request request);

    @FormUrlEncoded
    @POST("/api/dtl/v2/merchants/{id}/ratings")
    Void rate(@Path("id") String merchantId, @Field("rating") int stars,
              @Field("transaction_id") String transactionId);

}
