package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketBody;

public abstract class BaseReverseBucketUpdateBodyConverter<T extends BucketBody>
      extends BaseReverseBucketBodyConverter<T, BucketUpdateBody> {

   @Override
   public Class<BucketUpdateBody> targetClass() {
      return BucketUpdateBody.class;
   }
}
