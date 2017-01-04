package com.worldventures.dreamtrips.modules.mapping.converter;


import com.worldventures.dreamtrips.api.hashtags.model.Metadata;
import com.worldventures.dreamtrips.modules.feed.model.MetaData;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;

import java.util.ArrayList;

import io.techery.mappery.MapperyContext;

public class FeedMetaDataConverter implements Converter<Metadata, MetaData> {
   @Override
   public Class<Metadata> sourceClass() {
      return Metadata.class;
   }

   @Override
   public Class<MetaData> targetClass() {
      return MetaData.class;
   }

   @Override
   public MetaData convert(MapperyContext mapperyContext, Metadata metadata) {
      MetaData metaData = new MetaData();
      ArrayList<Hashtag> hashtagList = new ArrayList<>();
      hashtagList.addAll(mapperyContext.convert(metadata.hashtags(), Hashtag.class));
      metaData.setHashtags(hashtagList);

      return metaData;
   }
}
