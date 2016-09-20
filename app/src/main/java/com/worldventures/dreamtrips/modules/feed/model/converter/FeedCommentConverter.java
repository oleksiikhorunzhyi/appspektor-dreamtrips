package com.worldventures.dreamtrips.modules.feed.model.converter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public final class FeedCommentConverter implements Converter<com.worldventures.dreamtrips.api.comment.model.Comment, Comment> {
   @Override
   public Class<com.worldventures.dreamtrips.api.comment.model.Comment> sourceClass() {
      return com.worldventures.dreamtrips.api.comment.model.Comment.class;
   }

   @Override
   public Class<Comment> targetClass() {
      return Comment.class;
   }

   @Override
   public Comment convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.comment.model.Comment source) {
      Comment comment = new Comment();
      comment.setUid(source.uid());
      comment.setParentId(source.parentId());
      comment.setPostId(source.postId());
      comment.setMessage(source.text());
      comment.setUser(mapperyContext.convert(source.author(), User.class));
      comment.setCompany(source.company());
      comment.setCreatedAt(source.createdTime());
      comment.setUpdatedAt(source.updatedTime());
      comment.setLanguage(source.language());
      return comment;
   }
}
