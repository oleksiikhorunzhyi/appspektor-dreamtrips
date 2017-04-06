package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoTagParams {
    @SerializedName("target_user_id")
    int userId();

    @SerializedName("position")
    TagPosition position();
}
