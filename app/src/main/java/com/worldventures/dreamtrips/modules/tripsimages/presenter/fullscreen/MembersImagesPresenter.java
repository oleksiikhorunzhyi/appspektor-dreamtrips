package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.support.annotation.NonNull;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMemberPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * ALL MEMBERS PHOTOS. 1 TAB in Trip Images page.
 */
public class MembersImagesPresenter extends TripImagesListPresenter<MembersImagesPresenter.View> {

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;

   public MembersImagesPresenter() {
      this(TripImagesType.MEMBERS_IMAGES, 0);
   }

   public MembersImagesPresenter(TripImagesType type, int userId) {
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

   @Override
   protected SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentPage) {
      return new GetMemberPhotosQuery(PER_PAGE, currentPage);
   }

   @Override
   protected SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
      return new GetMemberPhotosQuery(PER_PAGE, 1);
   }

   public interface View extends TripImagesListPresenter.View {

      void openCreatePhoto(MediaAttachment mediaAttachment, CreateEntityBundle.Origin photoOrigin);
   }
}
