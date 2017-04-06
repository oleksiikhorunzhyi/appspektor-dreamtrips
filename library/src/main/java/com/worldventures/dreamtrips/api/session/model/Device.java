package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Gson.TypeAdapters
public interface Device {

    @Nullable
    @Value.Parameter
    @SerializedName("manufacturer")
    String manufacturer();

    @Nullable
    @Value.Parameter
    @SerializedName("market_name")
    String model();

}
