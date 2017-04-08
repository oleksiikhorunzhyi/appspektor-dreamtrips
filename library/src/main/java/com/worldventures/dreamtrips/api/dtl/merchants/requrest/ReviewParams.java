package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.File;
import java.util.List;

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
    @SerializedName("userId")
    String userId();
    @SerializedName("deviceFingerprint")
    String deviceFingerprint();
    @SerializedName("authorIpAddress")
    String authorIpAddress();
}
