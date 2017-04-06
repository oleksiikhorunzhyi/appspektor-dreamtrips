package com.worldventures.dreamtrips.api.post.model.response;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface Location {

    @SerializedName("name")
    @Nullable
    String name();
    @SerializedName("lat")
    @Nullable
    Double lat();
    @SerializedName("lng")
    @Nullable
    Double lng();

}
