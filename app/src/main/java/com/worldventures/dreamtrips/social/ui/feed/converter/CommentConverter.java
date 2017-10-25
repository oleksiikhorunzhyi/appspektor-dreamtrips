package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;

import io.techery.mappery.MapperyContext;

public class CommentConverter implements Converter<com.worldventures.dreamtrips.api.comment.model.Comment, Comment> {

   @Override
   public Comment convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.comment.model.Comment apiComment) {
      Comment comment = new Comment();
      comment.setUid(apiComment.uid());
      comment.setPostId(apiComment.postId());
      comment.setText(apiComment.text());
      comment.setUser(mapperyContext.convert(apiComment.author(), User.class));
      comment.setParentId(apiComment.parentId());
      comment.setCreatedAt(apiComment.createdTime());
      comment.setUpdatedAt(apiComment.updatedTime());
      comment.setCompany(apiComment.company());
      comment.setLanguage(apiComment.language());
      return comment;
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.comment.model.Comment> sourceClass() {
      return com.worldventures.dreamtrips.api.comment.model.Comment.class;
   }

   @Override
   public Class<Comment> targetClass() {
      return Comment.class;
   }
}
