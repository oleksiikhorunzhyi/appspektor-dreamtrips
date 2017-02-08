package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface ReviewsMerchantsHolder {

   @SerializedName("reviewsMerchants")
   ReviewsMerchant reviewsMerchants();
}
