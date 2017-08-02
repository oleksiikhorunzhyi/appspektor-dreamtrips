package com.worldventures.dreamtrips.wallet.service;

import android.app.Activity;

import com.messenger.delegate.CropImageDelegate;

import java.io.File;

import rx.Notification;
import rx.Observable;


public class WalletCropImageServiceImpl extends CropImageDelegate implements WalletCropImageService {

   public WalletCropImageServiceImpl(Activity activity) {
      super(activity);
   }

   @Override
   public Observable<File> observeCropper() {
      return getCroppedImagesStream()
            .filter(cropNotification -> cropNotification.getKind() == Notification.Kind.OnNext)
            .map(Notification::getValue);
   }
}
