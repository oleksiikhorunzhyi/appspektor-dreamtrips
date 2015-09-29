package com.worldventures.dreamtrips.modules.feed.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BaseFeedEntity implements FeedEntity {

    protected String uid;

    protected List<Comment> comments;
    protected int commentsCount;
    protected boolean liked;
    protected int likesCount;
    protected User user;

    private String firstUserLikedItem;

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

    @Override
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
    public int getLikesCount() {
        return likesCount;
    }

    @Override
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseFeedEntity that = (BaseFeedEntity) o;

        return uid.equals(that.uid);

    }

    @Override
    public String getFirstUserLikedItem() {
        return firstUserLikedItem;
    }

    @Override
    public void setFirstUserLikedItem(String firstUserLikedItem) {
        this.firstUserLikedItem = firstUserLikedItem;
    }

    @Override
    public void updateSocialContent(FeedEntity feedEntity) {
        setLikesCount(feedEntity.getLikesCount());
        setCommentsCount(feedEntity.getCommentsCount());
        setUser(feedEntity.getUser());
        setLiked(feedEntity.isLiked());
        setComments(feedEntity.getComments());
        setFirstUserLikedItem(feedEntity.getFirstUserLikedItem());
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

}
