package com.worldventures.dreamtrips.core.utils.events;

import com.kbeanie.imagechooser.api.ChosenImage;

public class ImagePickedEvent {

   private int requestType;
   private int requesterID;
   private ChosenImage[] images;

   public ImagePickedEvent(int requestType, int requesterID, ChosenImage[] images) {
      this.requestType = requestType;
      this.requesterID = requesterID;
      this.images = images;
   }

   public int getRequesterID() {
      return requesterID;
   }

   public ChosenImage[] getImages() {
      return images;
   }

   public int getRequestType() {
      return requestType;
   }
}
