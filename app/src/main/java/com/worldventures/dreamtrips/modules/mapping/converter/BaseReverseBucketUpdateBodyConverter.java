package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody;

public abstract class BaseReverseBucketUpdateBodyConverter<T extends BucketBody>
      extends BaseReverseBucketBodyConverter<T, BucketUpdateBody> {

   @Override
   public Class<BucketUpdateBody> targetClass() {
      return BucketUpdateBody.class;
   }
}
