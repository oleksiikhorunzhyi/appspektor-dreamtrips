package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface BucketUpdateBody {
    @Nullable
    @SerializedName("name")
    String name();

    @Nullable
    @SerializedName("description")
    String description();

    @Nullable
    @SerializedName("category_id")
    Integer categoryId();

    @Nullable
    @SerializedName("type")
    BucketType type();

    @Nullable
    @SerializedName("status")
    BucketStatus status();

    @Nullable
    @SerializedName("target_date")
    Date targetDate();

    @Nullable
    @SerializedName("tags")
    List<String> tags();

    @Nullable
    @SerializedName("friends")
    List<String> friends();

    @Nullable
    @SerializedName("cover_photo_id")
    String coverPhotoId();
}
