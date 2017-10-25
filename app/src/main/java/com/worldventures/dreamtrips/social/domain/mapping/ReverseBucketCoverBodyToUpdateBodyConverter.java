package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketUpdateBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketCoverBody;

import io.techery.mappery.MapperyContext;

public class ReverseBucketCoverBodyToUpdateBodyConverter extends BaseReverseBucketUpdateBodyConverter<BucketCoverBody> {

   @Override
   public Class<BucketCoverBody> sourceClass() {
      return BucketCoverBody.class;
   }

   @Override
   public BucketUpdateBody convert(MapperyContext mapperyContext, BucketCoverBody bucketBody) {
      return ImmutableBucketUpdateBody.builder()
            .coverPhotoId(bucketBody.coverId())
            .status(mapBucketStatus(bucketBody.status())).build();
   }
}
