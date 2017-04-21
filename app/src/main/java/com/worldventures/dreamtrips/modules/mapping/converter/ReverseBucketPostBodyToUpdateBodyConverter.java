package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketUpdateBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketPostBody;

import io.techery.mappery.MapperyContext;

public class ReverseBucketPostBodyToUpdateBodyConverter extends BaseReverseBucketUpdateBodyConverter<BucketPostBody> {

   @Override
   public Class<BucketPostBody> sourceClass() {
      return BucketPostBody.class;
   }

   @Override
   public BucketUpdateBody convert(MapperyContext mapperyContext, BucketPostBody bucketBody) {
      ImmutableBucketUpdateBody.Builder apiBody = ImmutableBucketUpdateBody.builder();
      apiBody.status(mapBucketStatus(bucketBody.status()));
      if (bucketBody.categoryId() != null) {
         apiBody.categoryId(bucketBody.categoryId());
      }
      apiBody.name(bucketBody.name());
      apiBody.targetDate(bucketBody.date());
      apiBody.description(bucketBody.description());
      apiBody.tags(bucketBody.tags());
      apiBody.friends(bucketBody.friends());
      return apiBody.build();
   }
}
