package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetInspireMePhotosCommand;

import io.techery.janet.ActionPipe;

public class InspireMePresenter extends TripImagesListPresenter<TripImagesListPresenter.View, GetInspireMePhotosCommand> {
   private static final double RESET_PHOTO_SEED_VALUE = 0d;

   protected double randomSeed;

   public InspireMePresenter(int userId) {
      super(TripImagesType.INSPIRE_ME, userId);
   }

   @Override
   public void onInjected() {
      super.onInjected();
      randomSeed = db.getLastUsedInspireMeRandomSeed();
      if (randomSeed == RESET_PHOTO_SEED_VALUE) {
         // generate seed in range from -1 to 1
         randomSeed = Math.random() * 2 - 1;
         db.saveLastUsedInspireMeRandomSeed(randomSeed);
      }
   }

   @Override
   public void dropView() {
      if (!view.isFullscreenView()) {
         db.saveLastUsedInspireMeRandomSeed(RESET_PHOTO_SEED_VALUE);
      }
      super.dropView();
   }

   @Override
   protected ActionPipe<GetInspireMePhotosCommand> getLoadingPipe() {
      return tripImagesInteractor.getInspireMePhotosPipe();
   }

   @Override
   protected GetInspireMePhotosCommand getReloadCommand() {
      return new GetInspireMePhotosCommand(randomSeed, 1, PER_PAGE);
   }

   @Override
   protected GetInspireMePhotosCommand getLoadMoreCommand(int currentCount) {
      return new GetInspireMePhotosCommand(randomSeed, currentCount / PER_PAGE + 1, PER_PAGE);
   }
}
