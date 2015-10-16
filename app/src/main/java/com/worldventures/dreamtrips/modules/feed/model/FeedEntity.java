package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.io.Serializable;
import java.util.List;

public interface FeedEntity extends Serializable, UidItem {

    String place();

    int getCommentsCount();

    void setCommentsCount(int count);

    List<Comment> getComments();

    void setComments(List<Comment> comments);

    void setLikesCount(int count);

    int getLikesCount();

    String getFirstUserLikedItem();

    void setFirstUserLikedItem(String  fullName);

    boolean isLiked();

    void setLiked(boolean isLiked);

    User getUser();

    void setUser(User user);

}
