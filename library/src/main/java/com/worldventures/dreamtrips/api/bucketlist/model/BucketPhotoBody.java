package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface BucketPhotoBody {

    @SerializedName("origin_url")
    String originUrl();

    @Nullable
    @SerializedName("title")
    String title();

    @Nullable
    @SerializedName("tags")
    List<String> tags();

    @Nullable
    @SerializedName("location_name")
    String location();

    @Nullable
    @SerializedName("coordinates")
    BucketPhotoLocation coordinates();

    @Nullable
    @SerializedName("shot_at")
    Date createdAt();


}


