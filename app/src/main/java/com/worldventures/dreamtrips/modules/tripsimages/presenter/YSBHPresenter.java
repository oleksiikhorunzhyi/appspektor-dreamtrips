package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetYSBHPhotosCommand;

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
      return new GetYSBHPhotosCommand(1, getPageSize());
   }

   @Override
   protected GetYSBHPhotosCommand getLoadMoreCommand(int currentCount) {
      return new GetYSBHPhotosCommand(currentCount / getPageSize() + 1, getPageSize());
   }

   @Override
   public void onItemClick(IFullScreenObject image) {
      super.onItemClick(image);
      TrackingHelper.viewTripImage(TrackingHelper.ACTION_YSHB_IMAGES, image.getFSId());
   }
}
