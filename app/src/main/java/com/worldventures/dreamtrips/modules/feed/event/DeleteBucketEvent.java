package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class DeleteBucketEvent {

   private BucketItem bucketItem;

   public DeleteBucketEvent(BucketItem bucketItem) {
      this.bucketItem = bucketItem;
   }

   public BucketItem getEntity() {
      return bucketItem;
   }
}
