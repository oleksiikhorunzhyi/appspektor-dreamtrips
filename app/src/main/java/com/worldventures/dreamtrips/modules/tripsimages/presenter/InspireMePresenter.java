package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetInspireMePhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import io.techery.janet.ActionPipe;

public class InspireMePresenter extends TripImagesListPresenter<TripImagesListPresenter.View, GetInspireMePhotosCommand> {
   protected double randomSeed;

   public InspireMePresenter(int userId) {
      super(TripImagesType.INSPIRE_ME, userId);
      // generate seed in range from -1 to 1
      randomSeed = Math.random() * 2 - 1;
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