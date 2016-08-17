package com.worldventures.dreamtrips.modules.feed.event;

public class AttachPhotoEvent {

   private int requestType;

   public AttachPhotoEvent(int requestType) {
      this.requestType = requestType;
   }

   public int getRequestType() {
      return requestType;
   }
}
