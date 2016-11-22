package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketUpdateBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBodyImpl;

import io.techery.mappery.MapperyContext;

public class ReverseBucketUpdateBodyConverter extends BaseReverseBucketUpdateBodyConverter<BucketBodyImpl> {

   @Override
   public Class<BucketBodyImpl> sourceClass() {
      return BucketBodyImpl.class;
   }

   @Override
   public BucketUpdateBody convert(MapperyContext mapperyContext, BucketBodyImpl bucketBody) {
      return ImmutableBucketUpdateBody.builder()
               .status(mapBucketStatus(bucketBody.status())).build();
   }
}
