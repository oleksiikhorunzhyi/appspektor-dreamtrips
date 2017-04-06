package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Currency {

    @SerializedName("code")
    String code();
    @SerializedName("prefix")
    String prefix();
    @SerializedName("suffix")
    String suffix();
    @SerializedName("name")
    String name();
    @SerializedName("default")
    Boolean isDefault();
}
