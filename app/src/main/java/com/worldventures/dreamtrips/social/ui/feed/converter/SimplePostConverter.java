package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.dreamtrips.api.post.model.response.PostSimple;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;

import io.techery.mappery.MapperyContext;

public class SimplePostConverter extends PostConverter<PostSimple> {

   @Override
   public Class<PostSimple> sourceClass() {
      return PostSimple.class;
   }

   @Override
   public TextualPost convert(MapperyContext mapperyContext, PostSimple apiPost) {
      return super.convert(mapperyContext,  apiPost);
   }
}
