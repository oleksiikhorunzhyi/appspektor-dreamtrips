package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface OperationHours {

    @SerializedName("from")
    String fromTime();
    @SerializedName("to")
    String toTime();
}
