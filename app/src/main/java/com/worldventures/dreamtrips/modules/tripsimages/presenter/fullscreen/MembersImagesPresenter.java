package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

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
               CreateEntityBundle.Origin origin; //analytics requires track origin of routing
               switch (type) {
                  case ACCOUNT_IMAGES_FROM_PROFILE:
                     origin = CreateEntityBundle.Origin.TRIP_IMAGES_PROFILE;
                     break;
                  case ACCOUNT_IMAGES:
                     origin = CreateEntityBundle.Origin.TRIP_IMAGES_MY;
                     break;
                  default:
                     origin = CreateEntityBundle.Origin.TRIP_IMAGES_MEMBER;
                     break;
               }
               view.openCreatePhoto(mediaAttachment, origin);
            });
   }

   @Override
   protected SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
      return new GetMemberPhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
   }

   @Override
   protected SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
      return new GetMemberPhotosQuery(PER_PAGE, 1);
   }

   public interface View extends TripImagesListPresenter.View {

      void openCreatePhoto(MediaAttachment mediaAttachment, CreateEntityBundle.Origin photoOrigin);
   }
}
