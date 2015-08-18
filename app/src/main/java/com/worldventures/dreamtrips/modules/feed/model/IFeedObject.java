package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.Date;
import java.util.List;

public interface IFeedObject {

    String place();

    long getUid();

    Date getCreatedAt();

    int commentsCount();

    List<Comment> getComments();
}
