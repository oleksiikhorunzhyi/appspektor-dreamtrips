package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketItemPhotoAnalyticEvent {

   String actionAttribute;
   String bucketItemId;

   public BucketItemPhotoAnalyticEvent(String actionAttribute, String bucketItemId) {
      this.actionAttribute = actionAttribute;
      this.bucketItemId = bucketItemId;
   }

   public String getActionAttribute() {
      return actionAttribute;
   }

   public String getBucketItemId() {
      return bucketItemId;
   }
}
