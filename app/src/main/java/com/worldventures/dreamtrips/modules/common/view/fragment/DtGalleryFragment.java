package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.feed.model.PickerIrregularPhotoModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PickerIrregularPhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

public class DtGalleryFragment extends BasePickerFragment<GalleryPresenter> implements GalleryPresenter.View {
   @Inject PermissionDispatcher permissionDispatcher;
   private Subscription permissionSubscription;

   @Override
   protected void registerCells() {
      adapter.registerCell(PhotoGalleryModel.class, PhotoGalleryCell.class);
      adapter.registerCell(PickerIrregularPhotoModel.class, PickerIrregularPhotoCell.class);
   }

   @Override
   protected int getPhotosType() {
      return PickImageDelegate.PICK_PICTURE;
   }

   @Override
   protected GalleryPresenter createPresenter(Bundle savedInstanceState) {
      return new GalleryPresenter();
   }

   @Override
   public void addItems(List<BasePhotoPickerModel> items) {
      adapter.addItem(new PickerIrregularPhotoModel(PickerIrregularPhotoModel.CAMERA, R.drawable.ic_picker_camera, R.string.camera, R.color.share_camera_color));
      adapter.addItem(new PickerIrregularPhotoModel(PickerIrregularPhotoModel.FACEBOOK, R.drawable.fb_logo, R.string.add_from_facebook, R.color.facebook_color));
      super.addItems(items);
   }

   @Override
   public void openFacebookAlbums() {
      photoPickerDelegate.openFacebookAlbums();
   }

   @Override
   public void checkPermissions() {
      permissionSubscription = permissionDispatcher.requestPermission(PermissionConstants.CAMERA_STORE_PERMISSIONS)
            .subscribe(new PermissionSubscriber().onPermissionRationaleAction(this::showRationaleForCamera)
                  .onPermissionGrantedAction(this::cameraPermissionGranted)
                  .onPermissionDeniedAction(this::showDeniedForCamera));
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      if (permissionSubscription != null && !permissionSubscription.isUnsubscribed()) {
         permissionSubscription.unsubscribe();
      }
   }

   void cameraPermissionGranted() {
      getPresenter().openCamera();
   }

   void showRationaleForCamera() {
      Snackbar.make(getView(), R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
   }

   void showDeniedForCamera() {
      Snackbar.make(getView(), R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
   }
}
