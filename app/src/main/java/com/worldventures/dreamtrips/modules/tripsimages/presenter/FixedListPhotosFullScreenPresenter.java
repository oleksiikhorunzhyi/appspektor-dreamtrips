package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TripImagesCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;

public class FixedListPhotosFullScreenPresenter extends TripImagesListPresenter<TripImagesListPresenter.View, TripImagesCommand<? extends IFullScreenObject>> {

   private ArrayList<IFullScreenObject> photos;
   private int notificationId;

   @Inject NotificationFeedInteractor notificationFeedInteractor;

   public FixedListPhotosFullScreenPresenter(ArrayList<IFullScreenObject> photos, int userId, int notificationId) {
      super(TripImagesType.FIXED, userId);
      this.photos = photos;
      this.notificationId = notificationId;
   }

   @Override
   public void onStart() {
      super.onStart();
      if (notificationId != UserBundle.NO_NOTIFICATION)
         notificationFeedInteractor.markNotificationPipe().send(new MarkNotificationAsReadCommand(notificationId));
   }

   @Override
   protected void syncPhotosAndUpdatePosition() {
      super.photos.addAll(photos);
   }

   @Override
   protected ActionPipe<TripImagesCommand<? extends IFullScreenObject>> getLoadingPipe() {
      return null;
   }

   @Override
   protected TripImagesCommand<? extends IFullScreenObject> getReloadCommand() {
      return null;
   }

   @Override
   protected TripImagesCommand<? extends IFullScreenObject> getLoadMoreCommand(int currentCount) {
      return null;
   }
}
