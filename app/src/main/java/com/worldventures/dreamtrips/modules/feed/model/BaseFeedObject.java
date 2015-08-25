package com.worldventures.dreamtrips.modules.feed.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BaseFeedObject implements IFeedObject, Serializable {

    protected String uid;

    protected List<Comment> comments;
    protected int commentsCount;
    protected boolean liked;
    protected int likesCount;

    @Override
    public String place() {
        return null;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public int getCommentsCount() {
        return commentsCount;
    }

    @Override
    public void setCommentsCount(int count) {
        commentsCount = count;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseFeedObject that = (BaseFeedObject) o;

        return uid.equals(that.uid);

    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

}
