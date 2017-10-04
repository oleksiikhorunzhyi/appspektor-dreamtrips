package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketBody;

public abstract class BaseReverseBucketPostBodyConverter<T extends BucketBody>
   extends BaseReverseBucketBodyConverter<T, BucketCreationBody> {

   @Override
   public Class<BucketCreationBody> targetClass() {
      return BucketCreationBody.class;
   }
}
