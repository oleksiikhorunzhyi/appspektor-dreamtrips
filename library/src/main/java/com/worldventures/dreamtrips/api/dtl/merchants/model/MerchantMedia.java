package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MerchantMedia {

    @SerializedName("url")
    String url();
    @SerializedName("category")
    String category();
    @SerializedName("width")
    Integer width();
    @SerializedName("height")
    Integer height();
}
