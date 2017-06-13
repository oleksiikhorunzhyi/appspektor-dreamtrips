package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetUserPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import io.techery.janet.ActionPipe;

public class AccountImagesPresenter extends MembersImagesBasePresenter<GetUserPhotosCommand> {

   public AccountImagesPresenter(TripImagesType type, int userId) {
      super(type, userId);
   }

   @Override
   protected ActionPipe<GetUserPhotosCommand> getLoadingPipe() {
      return tripImagesInteractor.getUserPhotosPipe();
   }

   @Override
   protected GetUserPhotosCommand getReloadCommand() {
      return new GetUserPhotosCommand(userId, 1, getPageSize());
   }

   @Override
   protected GetUserPhotosCommand getLoadMoreCommand(int currentCount) {
      return new GetUserPhotosCommand(userId, currentCount / getPageSize() + 1, getPageSize());
   }

   @Override
   public void onShowNewImagesClick() {
      // nothing to do here
   }
}
