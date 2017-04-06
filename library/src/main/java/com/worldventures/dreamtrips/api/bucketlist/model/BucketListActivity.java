package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface BucketListActivity extends Identifiable<Integer> {

    @SerializedName("name")
    String name();

    @Nullable
    @SerializedName("description")
    String description();

    @Nullable
    @SerializedName("short_description")
    String shortDescription();

    @Nullable
    @SerializedName("cover_photo")
    BucketCoverPhoto coverPhoto();

    @Nullable
    @SerializedName("url")
    String url();
}
