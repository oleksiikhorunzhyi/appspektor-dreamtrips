package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoCreationParams {
    @SerializedName("origin_url")
    String originURL();

    @SerializedName("width")
    Integer width();

    @SerializedName("height")
    Integer height();

    @SerializedName("title")
    @Nullable
    String title();

    @SerializedName("shot_at")
    @Nullable
    Date shotAt();

    @SerializedName("coordinates")
    @Nullable
    Coordinate coordinate();

    @SerializedName("tags")
    @Nullable
    List<String> tags();

    @SerializedName("photo_tags")
    @Nullable
    List<PhotoTagParams> photoTags();

    @SerializedName("location_name")
    @Nullable
    String locationName();
}
