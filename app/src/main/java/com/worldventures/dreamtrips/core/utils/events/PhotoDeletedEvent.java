package com.worldventures.dreamtrips.core.utils.events;

public class PhotoDeletedEvent {
   private String photoId;

   public PhotoDeletedEvent(String photoId) {
      this.photoId = photoId;
   }

   public String getPhotoId() {
      return photoId;
   }
}
