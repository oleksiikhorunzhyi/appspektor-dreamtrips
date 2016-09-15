package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketItemAnalyticEvent {

   String bucketItemId;
   String actionAttribute;

   public BucketItemAnalyticEvent(String bucketItemId, String actionAttribute) {
      this.bucketItemId = bucketItemId;
      this.actionAttribute = actionAttribute;
   }

   public String getBucketItemId() {
      return bucketItemId;
   }

   public String getActionAttribute() {
      return actionAttribute;
   }

}
