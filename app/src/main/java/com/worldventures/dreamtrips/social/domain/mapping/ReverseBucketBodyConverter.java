package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketCreationBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketBodyImpl;

import io.techery.mappery.MapperyContext;

public class ReverseBucketBodyConverter extends BaseReverseBucketPostBodyConverter<BucketBodyImpl> {

   @Override
   public Class<BucketBodyImpl> sourceClass() {
      return BucketBodyImpl.class;
   }

   @Override
   public BucketCreationBody convert(MapperyContext mapperyContext, BucketBodyImpl bucketBody) {
      ImmutableBucketCreationBody.Builder apiBody = ImmutableBucketCreationBody.builder();
      apiBody.id(bucketBody.id());
      apiBody.type(bucketBody.type());
      apiBody.status(mapBucketStatus(bucketBody.status()));
      return apiBody.build();
   }
}
