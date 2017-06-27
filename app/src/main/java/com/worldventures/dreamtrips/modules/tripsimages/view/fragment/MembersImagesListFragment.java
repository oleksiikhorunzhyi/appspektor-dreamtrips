package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.UploadingPostsSectionCell;
import com.worldventures.dreamtrips.modules.media_picker.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.UploadingCellDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MembersImagesBasePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MembersImagesPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_images_list)
public class MembersImagesListFragment<P extends MembersImagesBasePresenter> extends TripImagesListFragment<P> implements MembersImagesPresenter.View {

   public static final int MEDIA_PICKER_ITEMS_COUNT = 15;

   @InjectView(R.id.new_images_button) Button newImagesButton;

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
            .data(new PickerBundle.Builder().setPhotoPickLimit(MEDIA_PICKER_ITEMS_COUNT).build())
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

   @Override
   public void setImages(List<IFullScreenObject> images, UploadingPostsList uploadingPostsList) {
      List items = new ArrayList();
      if (!uploadingPostsList.getPhotoPosts().isEmpty()) {
         items.add(uploadingPostsList);
      }
      layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
         @Override
         public int getSpanSize(int position) {
            if (adapter.getItem(position) instanceof UploadingPostsList) {
               return getSpanCount();
            }
            return 1;
         }
      });
      items.addAll(images);
      adapter.setItems(items);
      adapter.notifyDataSetChanged();
   }

   @Override
   protected void registerCellsAndDelegates() {
      super.registerCellsAndDelegates();
      adapter.registerCell(UploadingPostsList.class, UploadingPostsSectionCell.class);
      adapter.registerDelegate(UploadingPostsList.class, new UploadingCellDelegate(getPresenter(), getContext()));
   }

   private boolean isCreatePhotoAlreadyAttached() {
      return Queryable.from(getActivity().getSupportFragmentManager().getFragments())
            .firstOrDefault(fragment -> fragment instanceof CreateTripImageFragment) != null;
   }

   @Override
   public void showNewImagesButton(String newImagesCountString) {
      newImagesButton.setVisibility(View.VISIBLE);
      newImagesButton.setText(newImagesCountString);
   }

   @Override
   public void hideNewImagesButton() {
      newImagesButton.setVisibility(View.GONE);
   }

   @OnClick(R.id.new_images_button)
   public void onShowNewImagesClick() {
      getPresenter().onShowNewImagesClick();
   }
}
