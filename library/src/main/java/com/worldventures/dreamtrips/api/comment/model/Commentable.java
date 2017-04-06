package com.worldventures.dreamtrips.api.comment.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public interface Commentable {
    @SerializedName("comments_count")
    int commentsCount();
    @SerializedName("comments")
    List<Comment> comments();
}
