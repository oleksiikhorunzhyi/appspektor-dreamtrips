package com.worldventures.dreamtrips.api.profile.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.circles.model.Circle;
import com.worldventures.dreamtrips.api.session.model.MutualFriends;
import com.worldventures.dreamtrips.api.session.model.Relationship;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface PublicUserProfile extends UserProfile {

    ///////////////////////////////////////////////////////////////////////////
    // Content
    ///////////////////////////////////////////////////////////////////////////

    @SerializedName("trip_images_count")
    int tripImagesCount();

    @SerializedName("bucket_list_items_count")
    int bucketListItemsCount();

    ///////////////////////////////////////////////////////////////////////////
    // Social/Friends
    ///////////////////////////////////////////////////////////////////////////

    @SerializedName("relationship")
    @Nullable
    Relationship relationship();

    @SerializedName("friends_count")
    int friendsCount();

    @SerializedName("mutuals")
    @Nullable
    MutualFriends mutuals();

    @SerializedName("circles")
    @Nullable
    List<Circle> circles();

    @SerializedName("social_enabled")
    boolean socialEnabled();

}
