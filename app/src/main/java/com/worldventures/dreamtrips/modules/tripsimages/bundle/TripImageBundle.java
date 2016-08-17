package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;


public class TripImageBundle {

   boolean fullScreenMode;
   int userId;
   ArrayList<IFullScreenObject> photos;
   int currentPhotosPosition;

   public TripImageBundle(boolean fullScreenMode, int userId, ArrayList<IFullScreenObject> photos, int currentPhotosPosition) {
      this.fullScreenMode = fullScreenMode;
      this.userId = userId;
      this.photos = photos;
      this.currentPhotosPosition = currentPhotosPosition;
   }

   public boolean isFullScreenMode() {
      return fullScreenMode;
   }

   public int getUserId() {
      return userId;
   }

   public ArrayList<IFullScreenObject> getPhotos() {
      return photos;
   }

   public int getCurrentPhotosPosition() {
      return currentPhotosPosition;
   }
}
