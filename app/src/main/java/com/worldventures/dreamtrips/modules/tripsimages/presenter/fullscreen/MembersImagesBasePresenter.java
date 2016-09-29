package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.command.TripImagesCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;

import javax.inject.Inject;

/**
 * ALL MEMBERS PHOTOS. 1 TAB in Trip Images page.
 */
public abstract class MembersImagesBasePresenter<C extends TripImagesCommand<? extends IFullScreenObject>> extends TripImagesListPresenter<MembersImagesBasePresenter.View, C> {

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;

   public MembersImagesBasePresenter() {
      this(TripImagesType.MEMBERS_IMAGES, 0);
   }

   public MembersImagesBasePresenter(TripImagesType type, int userId) {
      super(type, userId);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      mediaPickerEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(mediaAttachment -> {
               if (view.isVisibleOnScreen()) //cause neighbour tab also catches this event
                  view.openCreatePhoto(mediaAttachment, getRoutingOrigin());
            });
   }

   @NonNull
   private CreateEntityBundle.Origin getRoutingOrigin() {
      switch (type) {
         case ACCOUNT_IMAGES_FROM_PROFILE:
            return CreateEntityBundle.Origin.PROFILE_TRIP_IMAGES;
         case ACCOUNT_IMAGES:
            return CreateEntityBundle.Origin.MY_TRIP_IMAGES;
         default:
            return CreateEntityBundle.Origin.MEMBER_TRIP_IMAGES;
      }
   }

   public interface View extends TripImagesListPresenter.View {

      void openCreatePhoto(MediaAttachment mediaAttachment, CreateEntityBundle.Origin photoOrigin);
   }
}
