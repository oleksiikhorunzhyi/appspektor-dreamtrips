package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Errors {

    @Nullable
    @SerializedName("message")
    String message();

    @Nullable
    @SerializedName("code")
    String code();

    @Nullable
    @SerializedName("innerError")
    List<InnerError> innerError();
}
