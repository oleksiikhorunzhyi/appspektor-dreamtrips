package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.hashtags.model.HashTagSimple;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

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
