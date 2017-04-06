package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.HasLanguage;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface BucketItem extends Identifiable<Integer>, UniqueIdentifiable, HasLanguage {

    @SerializedName("name")
    String name();

    @Nullable
    @SerializedName("description")
    String description();

    @Nullable
    @SerializedName("category")
    BucketCategory category();

    @SerializedName("type")
    BucketType type();

    @SerializedName("status")
    BucketStatus status();

    @Nullable
    @SerializedName("location")
    BucketListLocation location();

    @Nullable
    @SerializedName("dining")
    BucketListDining dining();

    @Nullable
    @SerializedName("activity")
    BucketListActivity activity();

    @SerializedName("created_at")
    Date creationDate();

    @Nullable
    @SerializedName("target_date")
    Date targetDate();

    @Nullable
    @SerializedName("completion_date")
    Date completionDate();

    @Nullable
    @SerializedName("cover_photo")
    BucketCoverPhoto bucketCoverPhoto();

    @SerializedName("photos")
    List<BucketPhoto> bucketPhoto();

    @SerializedName("link")
    String link();

    @SerializedName("tags")
    List<BucketTag> tags();

    @SerializedName("friends")
    List<String> friends();
}
