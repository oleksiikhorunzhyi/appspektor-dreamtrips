package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetYSBHPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.command.GetYSBHPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.command.TripImagesCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

import io.techery.janet.ActionPipe;

public class YSBHPresenter extends TripImagesListPresenter<TripImagesListPresenter.View, GetYSBHPhotosCommand> {
   public YSBHPresenter(int userId) {
      super(TripImagesType.YOU_SHOULD_BE_HERE, userId);
   }

   @Override
   protected ActionPipe<GetYSBHPhotosCommand> getLoadingPipe() {
      return tripImagesInteractor.getYSBHPhotosPipe();
   }

   @Override
   protected GetYSBHPhotosCommand getReloadCommand() {
      return new GetYSBHPhotosCommand(1, PER_PAGE);
   }

   @Override
   protected GetYSBHPhotosCommand getLoadMoreCommand(int currentCount) {
      return new GetYSBHPhotosCommand(currentCount / PER_PAGE + 1, PER_PAGE);
   }
}