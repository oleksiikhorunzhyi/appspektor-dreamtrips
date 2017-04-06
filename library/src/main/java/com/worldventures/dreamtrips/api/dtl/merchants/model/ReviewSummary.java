package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface ReviewSummary {

   @SerializedName("total")
   String total();

   @SerializedName("ratingAverage")
   String ratingAverage();

   @SerializedName("userHasPendingReview")
   boolean userHasPendingReview();
}
