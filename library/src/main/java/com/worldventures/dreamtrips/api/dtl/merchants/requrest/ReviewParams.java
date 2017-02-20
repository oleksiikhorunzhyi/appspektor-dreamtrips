package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface ReviewParams {

   @SerializedName("userEmail")
   String userEmail();
   @SerializedName("userNickName")
   String userNickName();
   @SerializedName("reviewText")
   String reviewText();
   @SerializedName("rating")
   String rating();
   @SerializedName("verified")
   Boolean verified();
}
