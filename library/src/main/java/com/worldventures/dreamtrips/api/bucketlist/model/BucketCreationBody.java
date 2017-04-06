package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface BucketCreationBody {
    @Nullable
    @SerializedName("name")
    String name();

    @Nullable
    @SerializedName("id")
    String id(); // must be string because of trip uids. Name or Id is required

    @Nullable
    @SerializedName("description")
    String description();

    @Nullable
    @SerializedName("category_id")
    Integer categoryId();

    @SerializedName("type")
    String type(); // the same items as in BucketType enum + Trip

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
}
