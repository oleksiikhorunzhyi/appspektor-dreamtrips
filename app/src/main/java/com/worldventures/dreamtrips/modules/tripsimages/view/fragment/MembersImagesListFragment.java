package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesBasePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesPresenter;

import butterknife.OnClick;

@Layout(R.layout.fragment_account_images_list)
public class MembersImagesListFragment<P extends MembersImagesBasePresenter> extends TripImagesListFragment<P> implements MembersImagesPresenter.View {

   public static final int MEDIA_PICKER_ITEMS_COUNT = 15;

   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);

      if (!isVisibleToUser) hidePhotoPicker();
   }

   @OnClick(R.id.fab_photo)
   public void actionPhoto() {
      showPhotoPicker();
      if (this instanceof AccountImagesListFragment) {
         TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MY_IMAGES);
      } else {
         TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MEMBER_IMAGES);
      }
   }

   private void showPhotoPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .data(new PickerBundle(0, MEDIA_PICKER_ITEMS_COUNT))
            .build());
   }

   private void hidePhotoPicker() {
      if (router == null) return;
      //
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
            .containerId(R.id.picker_container)
            .fragmentManager(getChildFragmentManager())
            .build());
   }

   @Override
   public void openCreatePhoto(MediaAttachment mediaAttachment, CreateEntityBundle.Origin photoOrigin) {
      if (isCreatePhotoAlreadyAttached()) return;
      //
      router.moveTo(Route.PHOTO_CREATE, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getActivity().getSupportFragmentManager())
            .containerId(R.id.container_details_floating)
            .data(new CreateEntityBundle(mediaAttachment, photoOrigin))
            .build());
   }

   private boolean isCreatePhotoAlreadyAttached() {
      return Queryable.from(getActivity().getSupportFragmentManager().getFragments())
            .firstOrDefault(fragment -> fragment instanceof CreateTripImageFragment) != null;
   }
}
