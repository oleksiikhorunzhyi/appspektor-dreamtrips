package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.net.Uri;
import android.os.Bundle;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

import java.io.File;
import java.util.concurrent.Callable;

import icepick.Icepick;
import icepick.State;
import rx.Notification;
import rx.Observable;
import rx.Subscription;

public class MediaPickerAdapter implements MediaPickerService {

   private final MessengerMediaPickerDelegate messengerMediaPickerDelegate;
   private final CropImageDelegate cropImageDelegate;
   private final Callable<String> callablePendingPathOnce = new Callable<String>() {
      @Override
      public String call() throws Exception {
         try {
            return pendingPath;
         } finally {
            clearPendingPath();
         }
      }
   };

   @State String pendingPath;
   private Subscription subscription;

   public MediaPickerAdapter(MessengerMediaPickerDelegate messengerMediaPickerDelegate, CropImageDelegate cropImageDelegate) {
      this.messengerMediaPickerDelegate = messengerMediaPickerDelegate;
      this.cropImageDelegate = cropImageDelegate;
      subscription = messengerMediaPickerDelegate.getImagePathsStream().subscribe(path -> pendingPath = path);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   @Override
   public void onRestoreInstanceState(Bundle savedInstanceState) {
      Icepick.restoreInstanceState(this, savedInstanceState);
   }

   @Override
   public void destroy() {
      cropImageDelegate.destroy();
      if (subscription != null && !subscription.isUnsubscribed()) {
         subscription.unsubscribe();
      }
   }

   @Override
   public void setPhotoPickerListener(PhotoPickerLayout.PhotoPickerListener photoPickerListener) {
      messengerMediaPickerDelegate.setPhotoPickerListener(photoPickerListener);
   }

   @Override
   public void pickPhoto() {
      clearPendingPath();
      messengerMediaPickerDelegate.showPhotoPicker();
   }

   @Override
   public void pickPhotos(int limit) {
      clearPendingPath();
      messengerMediaPickerDelegate.showMultiPhotoPicker(limit);
   }

   @Override
   public void crop(String filePath) {
      clearPendingPath();
      cropImageDelegate.cropImage(filePath);
   }

   @Override
   public void hidePicker() {
      messengerMediaPickerDelegate.hidePhotoPicker();
   }

   @Override
   public Observable<Uri> observePicker() {
      return messengerMediaPickerDelegate.getImagePathsStream()
            .startWith(Observable.fromCallable(callablePendingPathOnce))
            .filter(path -> path != null)
            .flatMap(path -> {
               if (!path.contains("://")) path = "file://" + path;
               return Observable.just(Uri.parse(path));
            })
            .doOnNext(path -> clearPendingPath());
   }

   @Override
   public Observable<File> observeCropper() {
      return cropImageDelegate.getCroppedImagesStream()
            .filter(cropNotification -> cropNotification.getKind() == Notification.Kind.OnNext)
            .map(Notification::getValue);
   }

   private void clearPendingPath() {
      this.pendingPath = null;
   }

}
