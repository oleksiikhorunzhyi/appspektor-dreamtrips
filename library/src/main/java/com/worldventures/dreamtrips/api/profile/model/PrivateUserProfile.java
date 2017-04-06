package com.worldventures.dreamtrips.api.profile.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;


@Gson.TypeAdapters
@Value.Immutable
public interface PrivateUserProfile extends UserProfile {

    @SerializedName("dream_trips_points")
    double dreamTripsPoints();

    @SerializedName("rovia_bucks")
    double roviaBucks();

    ///////////////////////////////////////////////////////////////////////////
    // Social/Friends
    ///////////////////////////////////////////////////////////////////////////

    @SerializedName("friends_count")
    int friendsCount();

    ///////////////////////////////////////////////////////////////////////////
    // Content
    ///////////////////////////////////////////////////////////////////////////

    @SerializedName("trip_images_count")
    int tripImagesCount();

    @SerializedName("bucket_list_items_count")
    int bucketListItemsCount();

}
