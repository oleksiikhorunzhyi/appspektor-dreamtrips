package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketUpdateBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketBodyImpl;

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
