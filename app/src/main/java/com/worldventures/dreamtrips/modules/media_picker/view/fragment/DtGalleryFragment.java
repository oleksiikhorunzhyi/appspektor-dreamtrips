package com.worldventures.dreamtrips.modules.media_picker.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.media_picker.bundle.GalleryBundle;
import com.worldventures.dreamtrips.modules.feed.model.PickerIrregularPhotoModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.PickerIrregularPhotoCell;
import com.worldventures.dreamtrips.modules.media_picker.view.cell.PhotoPickerModelCell;
import com.worldventures.dreamtrips.modules.media_picker.view.cell.VideoPickerModelCell;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action0;

public class DtGalleryFragment extends BasePickerFragment<GalleryPresenter, GalleryBundle> implements GalleryPresenter.View {

   public static final int PICK_PICTURE_PHOTOS_TYPE = 291;

   @Inject PermissionDispatcher permissionDispatcher;
   private Subscription permissionSubscription;

   @Override
   protected void registerCells() {
      adapter.registerCell(PhotoPickerModel.class, PhotoPickerModelCell.class);
      adapter.registerDelegate(PhotoPickerModel.class, new CellDelegate<PhotoPickerModel>() {
         @Override
         public void onCellClicked(PhotoPickerModel model) {
            getPresenter().onPhotoPickerModelSelected(model);
         }
      });

      adapter.registerCell(VideoPickerModel.class, VideoPickerModelCell.class);
      adapter.registerDelegate(VideoPickerModel.class, new CellDelegate<VideoPickerModel>() {
         @Override
         public void onCellClicked(VideoPickerModel model) {
            getPresenter().onVideoPickerModelSelected(model);
         }
      });

      adapter.registerCell(PickerIrregularPhotoModel.class, PickerIrregularPhotoCell.class);
      adapter.registerDelegate(PickerIrregularPhotoModel.class, new CellDelegate<PickerIrregularPhotoModel>() {
         @Override
         public void onCellClicked(PickerIrregularPhotoModel model) {
            switch (model.getType()) {
               case PickerIrregularPhotoModel.CAMERA:
                  processCameraIconClick();
                  break;
               case PickerIrregularPhotoModel.FACEBOOK:
                  getPresenter().openFacebook();
                  break;
            }
         }
      });
   }

   private void processCameraIconClick() {
      getPresenter().onCameraIconClicked();
   }

   @Override
   public void showAttachmentTypeDialog() {
      final String[] items = new String[]{getString(R.string.camera_take_a_picture), getString(R.string.camera_record_a_video)};
      new MaterialDialog.Builder(getContext())
            .items(items)
            .itemsCallback((dialog, itemView, which, text) -> {
               switch (which) {
                  case 0:
                     getPresenter().tryOpenCameraForPhoto();
                     break;
                  case 1:
                     getPresenter().tryOpenCameraForVideo();
                     break;
               }
            }).show();
   }

   @Override
   protected int getPhotosType() {
      return PICK_PICTURE_PHOTOS_TYPE;
   }

   @Override
   protected GalleryPresenter createPresenter(Bundle savedInstanceState) {
      return new GalleryPresenter(getArgs().isVideoPickingEnabled());
   }

   @Override
   public void addItems(List<MediaPickerModel> items) {
      List staticItems = new ArrayList<>();
      staticItems.add(new PickerIrregularPhotoModel(PickerIrregularPhotoModel.CAMERA,
            R.drawable.ic_picker_camera, R.string.camera, R.color.share_camera_color));
      staticItems.add(new PickerIrregularPhotoModel(PickerIrregularPhotoModel.FACEBOOK,
            R.drawable.fb_logo, R.string.add_from_facebook, R.color.facebook_color));
      adapter.addItems(staticItems);
      super.addItems(items);
   }

   @Override
   public void openFacebookAlbums() {
      photoPickerDelegate.openFacebookAlbums();
   }

   @Override
   public void checkPermissionsForPhoto() {
      checkPermissionsForCamera(this::permissionToTakePhotoGranted);
   }

   @Override
   public void checkPermissionsForVideo() {
      checkPermissionsForCamera(this::permissionToRecordVideoGranted);
   }

   private void checkPermissionsForCamera(Action0 grantedAction) {
      permissionSubscription = permissionDispatcher.requestPermission(PermissionConstants.CAMERA_STORE_PERMISSIONS)
            .subscribe(new PermissionSubscriber().onPermissionRationaleAction(this::showRationaleForCamera)
                  .onPermissionGrantedAction(grantedAction)
                  .onPermissionDeniedAction(this::showDeniedForCamera));
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      if (permissionSubscription != null && !permissionSubscription.isUnsubscribed()) {
         permissionSubscription.unsubscribe();
      }
   }

   private void permissionToTakePhotoGranted() {
      getPresenter().openCameraForPhoto();
   }

   private void permissionToRecordVideoGranted() {
      getPresenter().openCameraForVideo();
   }

   private void showRationaleForCamera() {
      Snackbar.make(getView(), R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
   }

   private void showDeniedForCamera() {
      Snackbar.make(getView(), R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
   }
}
