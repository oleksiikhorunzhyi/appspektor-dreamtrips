package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody;

public abstract class BaseReverseBucketPostBodyConverter<T extends BucketBody>
   extends BaseReverseBucketBodyConverter<T, BucketCreationBody> {

   @Override
   public Class<BucketCreationBody> targetClass() {
      return BucketCreationBody.class;
   }
}
