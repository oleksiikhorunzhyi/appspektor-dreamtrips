package com.worldventures.dreamtrips.core.utils.events;

public class ImagePickRequestEvent {

   private int requestType;
   private int requesterID;

   public ImagePickRequestEvent(int requestType, int requesterID) {
      this.requestType = requestType;
      this.requesterID = requesterID;
   }

   public int getRequestType() {
      return requestType;
   }

   public int getRequesterID() {
      return requesterID;
   }
}
