package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple;

public class BucketItemSimpleConverter extends BucketItemConverter<BucketItemSimple> {

   @Override
   public Class<BucketItemSimple> sourceClass() {
      return BucketItemSimple.class;
   }
}
