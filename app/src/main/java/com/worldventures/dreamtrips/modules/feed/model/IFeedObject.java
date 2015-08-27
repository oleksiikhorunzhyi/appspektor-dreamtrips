package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.Date;
import java.util.List;

public interface IFeedObject {

    String place();

    String getUid();

    int getCommentsCount();

    void setCommentsCount(int count);

    List<Comment> getComments();

    void setComments(List<Comment> comments);

    void setLikesCount(int count);

    int getLikesCount();

    boolean isLiked();

    void setLiked(boolean isLiked);
}
