package com.worldventures.dreamtrips.modules.trips.event;

public class TripImageAnalyticEvent {

   String tripImageId;
   String actionAttribute;

   public TripImageAnalyticEvent(String tripImageId, String actionAttribute) {
      this.tripImageId = tripImageId;
      this.actionAttribute = actionAttribute;
   }

   public String getTripImageId() {
      return tripImageId;
   }

   public String getActionAttribute() {
      return actionAttribute;
   }
}
