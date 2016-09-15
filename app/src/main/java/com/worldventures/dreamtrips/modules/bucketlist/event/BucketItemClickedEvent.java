package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class BucketItemClickedEvent {

   private BucketItem bucketItem;

   public BucketItemClickedEvent(BucketItem bucketItem) {
      this.bucketItem = bucketItem;
   }

   public BucketItem getBucketItem() {
      return bucketItem;
   }

   public void setBucketItem(BucketItem bucketItem) {
      this.bucketItem = bucketItem;
   }
}
