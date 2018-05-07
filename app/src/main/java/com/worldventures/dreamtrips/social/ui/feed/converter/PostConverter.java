package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.api.post.model.response.Post;
import com.worldventures.core.model.Location;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.Hashtag;

import io.techery.mappery.MapperyContext;

public abstract class PostConverter<T extends Post> implements Converter<T, TextualPost> {

   @Override
   public Class<TextualPost> targetClass() {
      return TextualPost.class;
   }

   @Override
   public TextualPost convert(MapperyContext mapperyContext, T apiPost) {
      TextualPost post = new TextualPost();
      post.setUid(apiPost.uid());
      post.setDescription(apiPost.description());
      post.setOwner(mapperyContext.convert(apiPost.owner(), User.class));
      post.setAttachments(mapperyContext.convert(apiPost.attachments(), FeedEntityHolder.class));
      post.setLocation(mapperyContext.convert(apiPost.location(), Location.class));
      post.setLanguage(apiPost.language());
      post.setHashtags(mapperyContext.convert(apiPost.hashtags(), Hashtag.class));
      return post;
   }
}
