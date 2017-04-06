package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface ReviewText {

    @Nullable
    @SerializedName("Field")
    String field();

    @Nullable
    @SerializedName("Message")
    String message();

    @Nullable
    @SerializedName("Code")
    String code();
}
