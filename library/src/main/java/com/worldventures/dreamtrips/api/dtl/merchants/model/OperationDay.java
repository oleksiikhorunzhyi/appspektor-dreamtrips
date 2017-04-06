package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface OperationDay {

    @SerializedName("day_of_week")
    String dayOfWeek();
    @SerializedName("operation_hours")
    List<OperationHours> operationHours();
}
