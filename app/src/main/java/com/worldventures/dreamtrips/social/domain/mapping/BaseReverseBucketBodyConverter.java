package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketStatus;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketBody;

public abstract class BaseReverseBucketBodyConverter<T extends BucketBody, S> implements Converter<T, S> {

   protected BucketStatus mapBucketStatus(String status) {
      switch (status) {
         case com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.COMPLETED:
            return BucketStatus.COMPLETED;
         case com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.NEW:
            return BucketStatus.NEW;
      }
      throw new IllegalArgumentException("No such status");
   }
}
