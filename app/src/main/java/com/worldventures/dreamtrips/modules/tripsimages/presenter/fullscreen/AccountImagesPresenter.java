package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.command.GetUserPhotosCommand;
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
      return new GetUserPhotosCommand(userId, 1, PER_PAGE);
   }

   @Override
   protected GetUserPhotosCommand getLoadMoreCommand(int currentCount) {
      return new GetUserPhotosCommand(userId, currentCount / PER_PAGE + 1, PER_PAGE);
   }
}
