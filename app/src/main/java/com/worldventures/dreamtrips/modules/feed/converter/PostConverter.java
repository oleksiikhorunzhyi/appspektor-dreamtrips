package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.post.model.response.Post;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import io.techery.mappery.MapperyContext;

public abstract class PostConverter<T extends Post> implements Converter<T, TextualPost> {

   @Override
   public Class<TextualPost> targetClass() {
      return null;
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
