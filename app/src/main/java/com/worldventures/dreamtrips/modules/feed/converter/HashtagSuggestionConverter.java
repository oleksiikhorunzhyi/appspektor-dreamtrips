package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.hashtags.model.HashTagExtended;
import com.worldventures.dreamtrips.api.hashtags.model.HashTagSimple;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class HashtagSuggestionConverter implements Converter<HashTagExtended, HashtagSuggestion> {

   @Override
   public Class<HashTagExtended> sourceClass() {
      return HashTagExtended.class;
   }

   @Override
   public Class<HashtagSuggestion> targetClass() {
      return HashtagSuggestion.class;
   }

   @Override
   public HashtagSuggestion convert(MapperyContext mapperyContext, HashTagExtended hashTagExtended) {
      HashtagSuggestion hashtagSuggestion = new HashtagSuggestion();
      hashtagSuggestion.setName(hashTagExtended.name());
      hashtagSuggestion.setUsageCount(hashTagExtended.usageCount());
      return hashtagSuggestion;
   }
}
