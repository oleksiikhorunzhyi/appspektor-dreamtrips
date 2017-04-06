package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoUpdateParams {
    @Nullable
    @SerializedName("title")
    String title();

    @SerializedName("shot_at")
    Date shotAt();

    @SerializedName("coordinates")
    @Nullable
    Coordinate coordinate();

    @SerializedName("tags")
    @Nullable
    List<String> tags();

    @SerializedName("location_name")
    @Nullable
    String locationName();
}
