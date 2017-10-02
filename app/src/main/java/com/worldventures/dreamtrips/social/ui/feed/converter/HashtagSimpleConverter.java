package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.hashtags.model.HashTagSimple;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.Hashtag;

import io.techery.mappery.MapperyContext;

public class HashtagSimpleConverter implements Converter<HashTagSimple, Hashtag> {
   @Override
   public Class<HashTagSimple> sourceClass() {
      return HashTagSimple.class;
   }

   @Override
   public Class<Hashtag> targetClass() {
      return Hashtag.class;
   }

   @Override
   public Hashtag convert(MapperyContext mapperyContext, HashTagSimple hashTagSimple) {
      return new Hashtag(hashTagSimple.name());
   }
}
