package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketAnalyticEvent {

   String actionAttribute;

   public BucketAnalyticEvent(String actionAttribute) {
      this.actionAttribute = actionAttribute;
   }

   public String getActionAttribute() {
      return actionAttribute;
   }
}
