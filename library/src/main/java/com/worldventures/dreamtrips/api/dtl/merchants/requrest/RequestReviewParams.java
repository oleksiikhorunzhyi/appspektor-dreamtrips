package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface RequestReviewParams {

    @SerializedName("brandId")
    String brandId();
    @SerializedName("productId")
    String productId();
}
