package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.command.GetMembersPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;

/**
 * ALL MEMBERS PHOTOS. 1 TAB in Trip Images page.
 */
public class MembersImagesPresenter extends MembersImagesBasePresenter<GetMembersPhotosCommand> {

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;

   public MembersImagesPresenter() {
      this(TripImagesType.MEMBERS_IMAGES, 0);
   }

   public MembersImagesPresenter(TripImagesType type, int userId) {
      super(type, userId);
   }

   protected ActionPipe<GetMembersPhotosCommand> getLoadingPipe() {
      return tripImagesInteractor.getMembersPhotosPipe();
   }

   @Override
   protected GetMembersPhotosCommand getReloadCommand() {
      return new GetMembersPhotosCommand(1, PER_PAGE);
   }

   @Override
   protected GetMembersPhotosCommand getLoadMoreCommand(int currentCount) {
      return new GetMembersPhotosCommand(currentCount / PER_PAGE + 1, PER_PAGE);
   }
}
