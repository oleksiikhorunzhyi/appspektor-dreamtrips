package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketCreationBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketPostBody;

import io.techery.mappery.MapperyContext;

public class ReverseBucketPostBodyConverter extends BaseReverseBucketPostBodyConverter<BucketPostBody> {

   @Override
   public Class<BucketPostBody> sourceClass() {
      return BucketPostBody.class;
   }

   @Override
   public BucketCreationBody convert(MapperyContext mapperyContext, BucketPostBody bucketBody) {
      ImmutableBucketCreationBody.Builder apiBody = ImmutableBucketCreationBody.builder();
      apiBody.id(bucketBody.id());
      apiBody.type(bucketBody.type());
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
