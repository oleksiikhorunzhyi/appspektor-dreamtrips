package com.worldventures.dreamtrips.modules.feed.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TextualPost implements IFeedObject{

    private long uid;

    private String description;
    private List<Comment> comments;
    @SerializedName("comments_count")
    private int commentsCount;
    private boolean liked;
    private int likesCount;

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

    @Override
    public boolean isLiked() {
        return liked;
    }

    @Override
    public int likesCount() {
        return likesCount;
    }

}
