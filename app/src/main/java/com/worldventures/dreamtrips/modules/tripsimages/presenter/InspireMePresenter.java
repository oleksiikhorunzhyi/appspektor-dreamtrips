package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetInspireMePhotosCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;

public class InspireMePresenter extends TripImagesListPresenter<TripImagesListPresenter.View, GetInspireMePhotosCommand> {
   private static final double RESET_PHOTO_SEED_VALUE = 0d;

   protected double randomSeed;

   @Inject BackStackDelegate backStackDelegate;

   public InspireMePresenter(int userId) {
      super(TripImagesType.INSPIRE_ME, userId);
   }

   @Override
   public void takeView(View view) {
      randomSeed = db.getLastUsedInspireMeRandomSeed();
      if (randomSeed == RESET_PHOTO_SEED_VALUE) {
         generateSeed();
      }
      super.takeView(view);
      backStackDelegate.setListener(() -> {
         if (!view.isFullscreenView()) {
            db.saveLastUsedInspireMeRandomSeed(RESET_PHOTO_SEED_VALUE);
         }
         return false;
      });
   }

   private void generateSeed() {
      // generate seed in range from -1 to 1
      randomSeed = Math.random() * 2 - 1;
      db.saveLastUsedInspireMeRandomSeed(randomSeed);
   }

   @Override
   public void reload(boolean userInitiated) {
      if (userInitiated) {
         generateSeed();
      }
      super.reload(userInitiated);
   }

   @Override
   public void dropView() {
      super.dropView();
      backStackDelegate.setListener(null);
   }

   @Override
   protected ActionPipe<GetInspireMePhotosCommand> getLoadingPipe() {
      return tripImagesInteractor.getInspireMePhotosPipe();
   }

   @Override
   protected GetInspireMePhotosCommand getReloadCommand() {
      return new GetInspireMePhotosCommand(randomSeed, 1, getPageSize());
   }

   @Override
   protected GetInspireMePhotosCommand getLoadMoreCommand(int currentCount) {
      return new GetInspireMePhotosCommand(randomSeed, currentCount / getPageSize() + 1, getPageSize());
   }

   @Override
   public void onItemClick(IFullScreenObject image) {
      super.onItemClick(image);
      TrackingHelper.viewTripImage(TrackingHelper.ACTION_INSPIRE_ME_IMAGES, image.getFSId());
   }
}
