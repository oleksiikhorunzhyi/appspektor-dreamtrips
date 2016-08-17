package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

/**
 * 1 on 05.03.15.
 */
public class MarkBucketItemDoneEvent {
   private BucketItem bucketItem;
   private int position;

   public MarkBucketItemDoneEvent(BucketItem bucketItem, int position) {
      this.bucketItem = bucketItem;
      this.position = position;
   }

   public BucketItem getBucketItem() {
      return bucketItem;
   }

   public void setBucketItem(BucketItem bucketItem) {
      this.bucketItem = bucketItem;
   }

   public int getPosition() {
      return position;
   }

   public void setPosition(int position) {
      this.position = position;
   }
}
