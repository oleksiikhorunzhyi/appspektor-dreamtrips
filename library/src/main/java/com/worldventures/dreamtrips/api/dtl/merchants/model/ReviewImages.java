package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface ReviewImages {

    @Nullable
    @SerializedName("normalUrl")
    String normalUrl();

    @Nullable
    @SerializedName("thumbnailUrl")
    String thumbnailUrl();
}
