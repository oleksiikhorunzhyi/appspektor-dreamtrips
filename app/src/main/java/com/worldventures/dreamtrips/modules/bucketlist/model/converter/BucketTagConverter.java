package com.worldventures.dreamtrips.modules.bucketlist.model.converter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketTag;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class BucketTagConverter implements Converter<com.worldventures.dreamtrips.api.bucketlist.model.BucketTag, BucketTag> {

   @Override
   public Class<com.worldventures.dreamtrips.api.bucketlist.model.BucketTag> sourceClass() {
      return com.worldventures.dreamtrips.api.bucketlist.model.BucketTag.class;
   }

   @Override
   public Class<BucketTag> targetClass() {
      return BucketTag.class;
   }

   @Override
   public BucketTag convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.bucketlist.model.BucketTag bucketTag) {
      BucketTag tag = new BucketTag();
      tag.setName(bucketTag.name());
      return tag;
   }
}
