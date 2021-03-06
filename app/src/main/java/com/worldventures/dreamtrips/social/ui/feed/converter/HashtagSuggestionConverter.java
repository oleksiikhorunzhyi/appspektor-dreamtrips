package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.hashtags.model.HashTagExtended;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.HashtagSuggestion;

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
