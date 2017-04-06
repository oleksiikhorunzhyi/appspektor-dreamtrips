package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface BucketListDining extends Identifiable<Integer> {

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

    @Nullable
    @SerializedName("country")
    String country();

    @Nullable
    @SerializedName("city")
    String city();

    @Nullable
    @SerializedName("address")
    String address();

    @Nullable
    @SerializedName("cuisine")
    String cuisine();

    @Nullable
    @SerializedName("phone_number")
    String phone();

    @Nullable
    @SerializedName("price_range")
    String priceRange();
}
