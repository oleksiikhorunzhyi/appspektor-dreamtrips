package com.worldventures.dreamtrips.api.likes.model;

import com.google.gson.annotations.SerializedName;

public interface Likeable {
    @SerializedName("liked")
    boolean liked();
    @SerializedName("likes_count")
    int likes(); // todo rename to likesCount to be consistent with commentsCount()
}
