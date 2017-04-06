package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface SdkFlaggingReviewParams {

    @SerializedName("authorIpAddress")
    String authorIpAddress();

    @SerializedName("contentType")
    Integer contentType();

    @SerializedName("feedbackType")
    Integer feedbackType();
}
