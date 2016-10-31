package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import io.techery.mappery.MapperyContext;

public class PostSocializedConverter extends PostConverter<PostSocialized> {

   @Override
   public Class<PostSocialized> sourceClass() {
      return PostSocialized.class;
   }

   @Override
   public TextualPost convert(MapperyContext mapperyContext, PostSocialized apiPost) {
      TextualPost post = super.convert(mapperyContext, apiPost);

      post.setLiked(apiPost.liked());
      post.setLikesCount(apiPost.likes());

      post.setCommentsCount(apiPost.commentsCount());
      post.setComments(mapperyContext.convert(apiPost.comments(), Comment.class));

      return post;
   }
}
