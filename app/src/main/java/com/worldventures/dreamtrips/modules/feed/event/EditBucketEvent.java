package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class EditBucketEvent {
   private BucketItem bucketItem;

   private BucketItem.BucketType type;

   public EditBucketEvent(BucketItem bucketItem, BucketItem.BucketType type) {
      this.bucketItem = bucketItem;
      this.type = type;
   }

   public BucketItem.BucketType type() {
      return type;
   }

   public BucketItem bucketItem() {
      return bucketItem;
   }
}
