package com.worldventures.dreamtrips.modules.feed.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface FeedEntity extends Serializable, UidItem, TranslatableItem {

   User getOwner();

   String place();

   void setOwner(User user);

   int getCommentsCount();

   void setCommentsCount(int count);

   @NotNull
   List<Comment> getComments();

   void setComments(List<Comment> comments);

   void setLikesCount(int count);

   int getLikesCount();

   boolean isLiked();

   void setLiked(boolean isLiked);

   String getFirstLikerName();

   void setFirstLikerName(String fullName);

   void syncLikeState(FeedEntity feedEntity);

   @Nullable
   String getLanguage();

   boolean contentSame(FeedEntity feedEntity);

   Date getCreatedAt();
}
