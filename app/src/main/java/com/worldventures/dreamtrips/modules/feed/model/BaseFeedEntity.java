package com.worldventures.dreamtrips.modules.feed.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public abstract class BaseFeedEntity implements FeedEntity {

    protected String uid;
    @SerializedName("user")
    protected User owner;

    protected int commentsCount;
    protected List<Comment> comments;
    protected boolean liked;
    protected int likesCount;

    ///////////////////////////////////////////////////////////////////////////
    // Getters & Setters
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public int getCommentsCount() {
        return commentsCount;
    }

    @Override
    public void setCommentsCount(int count) {
        commentsCount = count;
    }

    @Override
    public List<Comment> getComments() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    @Override
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean isLiked() {
        return liked;
    }

    @Override
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    @Override
    public int getLikesCount() {
        return likesCount;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private String firstLikerName;

    @Override
    public String getFirstLikerName() {
        return firstLikerName;
    }

    @Override
    public void setFirstLikerName(String firstLikerName) {
        this.firstLikerName = firstLikerName;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseFeedEntity that = (BaseFeedEntity) o;

        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public String toString() {
        return "BaseFeedEntity{" +
                "likesCount=" + likesCount +
                ", liked=" + liked +
                ", commentsCount=" + commentsCount +
                ", comments=" + comments +
                ", owner=" + owner +
                ", uid='" + uid + '\'' +
                '}';
    }
}
