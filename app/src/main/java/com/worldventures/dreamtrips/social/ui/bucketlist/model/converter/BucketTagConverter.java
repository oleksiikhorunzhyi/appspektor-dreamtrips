package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketTag;

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
