package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface Location {

    @Nullable @SerializedName("city")
    String city();
    @Nullable @SerializedName("state")
    String state();
    @Nullable @SerializedName("country")
    String country();
    @SerializedName("ll")
    String coordinates();
}
