package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.feed.api.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class FixedListPhotosFullScreenPresenter extends TripImagesListPresenter<TripImagesListPresenter.View> {

   private ArrayList<IFullScreenObject> photos;
   private int notificationId;

   public FixedListPhotosFullScreenPresenter(ArrayList<IFullScreenObject> photos, int userId, int notificationId) {
      super(TripImagesType.FIXED, userId);
      this.photos = photos;
      this.notificationId = notificationId;
   }

   @Override
   public void onStart() {
      super.onStart();
      if (notificationId != UserBundle.NO_NOTIFICATION) {
         doRequest(new MarkNotificationAsReadCommand(notificationId), aVoid -> {
         });
      }
   }

   @Override
   protected void syncPhotosAndUpdatePosition() {
      super.photos.addAll(photos);
   }

   @Override
   protected SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
      return null;
   }

   @Override
   protected SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
      return null;
   }

}
