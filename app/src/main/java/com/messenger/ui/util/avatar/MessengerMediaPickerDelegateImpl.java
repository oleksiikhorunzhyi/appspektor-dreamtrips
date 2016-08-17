package com.messenger.ui.util.avatar;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.ui.helper.LegacyPhotoPickerDelegate;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionGrantedComposer;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class MessengerMediaPickerDelegateImpl implements MessengerMediaPickerDelegate {

   public static final int MESSENGER_MULTI_PICK_LIMIT = 15;

   private LegacyPhotoPickerDelegate legacyPhotoPickerDelegate;
   private PhotoPickerLayoutDelegate photoPickerLayoutDelegate;
   private PermissionDispatcher permissionDispatcher;

   private PublishSubject<String> imagesStream = PublishSubject.create();
   private Subscription cameraImagesStreamSubscription;

   public MessengerMediaPickerDelegateImpl(LegacyPhotoPickerDelegate legacyPhotoPickerDelegate, PhotoPickerLayoutDelegate photoPickerLayoutDelegate, PermissionDispatcher permissionDispatcher) {
      this.legacyPhotoPickerDelegate = legacyPhotoPickerDelegate;
      this.photoPickerLayoutDelegate = photoPickerLayoutDelegate;
      this.permissionDispatcher = permissionDispatcher;
      initPhotoPicker();
   }

   @Override
   public void register() {
      legacyPhotoPickerDelegate.register();
      cameraImagesStreamSubscription = legacyPhotoPickerDelegate.watchChosenImages().subscribe(photos -> {
         onImagesPicked(Queryable.from(photos).map(ChosenImage::getFilePathOriginal).toList());
      });
   }

   @Override
   public void unregister() {
      legacyPhotoPickerDelegate.unregister();
      cameraImagesStreamSubscription.unsubscribe();
   }

   @Override
   public void showPhotoPicker() {
      checkPermissions().subscribe(aVoid -> {
         if (!photoPickerLayoutDelegate.isPanelVisible()) {
            photoPickerLayoutDelegate.showPickerAfterDelay();
         }
      });
   }

   @Override
   public void showMultiPhotoPicker() {
      checkPermissions().subscribe(aVoid -> {
         if (!photoPickerLayoutDelegate.isPanelVisible()) {
            photoPickerLayoutDelegate.showPicker(true, MESSENGER_MULTI_PICK_LIMIT);
         } else {
            photoPickerLayoutDelegate.hidePicker();
         }
      });
   }

   private Observable<Void> checkPermissions() {
      return permissionDispatcher.requestPermission(PermissionConstants.STORE_PERMISSIONS, false)
            .compose(new PermissionGrantedComposer());
   }

   @Override
   public void hidePhotoPicker() {
      photoPickerLayoutDelegate.hidePicker();
   }

   @Override
   public Observable<String> getImagePathsStream() {
      return imagesStream;
   }

   private void initPhotoPicker() {
      photoPickerLayoutDelegate.setOnDoneClickListener((chosenImages, type) -> onImagesPicked(Queryable.from(chosenImages)
            .map(BasePhotoPickerModel::getOriginalPath)
            .toList()));
   }

   private void onImagesPicked(List<String> imagePaths) {
      photoPickerLayoutDelegate.hidePicker();
      Queryable.from(imagePaths).forEachR(path -> imagesStream.onNext(path));
   }
}
