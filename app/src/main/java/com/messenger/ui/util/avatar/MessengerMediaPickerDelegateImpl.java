package com.messenger.ui.util.avatar;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionGrantedComposer;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class MessengerMediaPickerDelegateImpl implements MessengerMediaPickerDelegate {

   public static final int MESSENGER_MULTI_PICK_LIMIT = 15;

   private PhotoPickerLayoutDelegate photoPickerLayoutDelegate;
   private PermissionDispatcher permissionDispatcher;
   private MediaInteractor mediaInteractor;

   private PublishSubject<String> imagesStream = PublishSubject.create();
   private Subscription cameraImagesStreamSubscription;

   public MessengerMediaPickerDelegateImpl(MediaInteractor mediaInteractor,
         PhotoPickerLayoutDelegate photoPickerLayoutDelegate,
         PermissionDispatcher permissionDispatcher) {
      this.mediaInteractor = mediaInteractor;
      this.photoPickerLayoutDelegate = photoPickerLayoutDelegate;
      this.permissionDispatcher = permissionDispatcher;
      initPhotoPicker();
   }

   @Override
   public void resetPhotoPicker() {
      initPhotoPicker();
   }

   @Override
   public void register() {
      cameraImagesStreamSubscription = mediaInteractor.imageCapturedPipe()
            .observeSuccess()
            .subscribe(imageCapturedCommand -> {
               onImagesPicked(Collections.singletonList(imageCapturedCommand.getResult()));
            });
   }

   @Override
   public void unregister() {
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
   public void setPhotoPickerListener(PhotoPickerLayout.PhotoPickerListener photoPickerListener) {
      photoPickerLayoutDelegate.setPhotoPickerListener(photoPickerListener);
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
