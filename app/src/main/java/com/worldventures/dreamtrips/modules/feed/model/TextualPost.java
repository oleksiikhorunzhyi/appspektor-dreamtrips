package com.worldventures.dreamtrips.modules.feed.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.Date;
import java.util.List;

public class TextualPost extends BaseEntity implements IFeedObject{

    private long uid;

    private String description;
    private List<Comment> comments;
    @SerializedName("comments_count")
    private int commentsCount;
    public String getDescription() {
        return description;
    }

    @Override
    public String place() {
        return null;
    }

    @Override
    public long getUid() {
        return uid;
    }

    @Override
    public Date getCreatedAt() {
        return null;
    }

    @Override
    public int commentsCount() {
        return commentsCount;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }
}
