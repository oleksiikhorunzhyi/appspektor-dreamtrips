package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface RatingParams {

    @SerializedName("rating")
    Integer rating();
    @SerializedName("transaction_id")
    String transactionId();
    @Nullable
    @SerializedName("comment")
    String comment();
}
