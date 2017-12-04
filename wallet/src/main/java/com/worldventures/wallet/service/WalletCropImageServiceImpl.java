package com.worldventures.wallet.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.ui.util.CroppingUtils;

import java.io.File;

import rx.Notification;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;


public class WalletCropImageServiceImpl implements WalletCropImageService {

   private static final String TEMP_PHOTO_DIR = "cropped_images";
   private final PublishSubject<Notification<File>> croppedImagesStream = PublishSubject.create();
   private final WeakHandler handler = new WeakHandler();

   @Override
   public Observable<File> observeCropper() {
      return croppedImagesStream
            .filter(cropNotification -> cropNotification.getKind() == Notification.Kind.OnNext)
            .map(Notification::getValue);
   }

   @Override
   public void cropImage(Activity activity, Uri uri) {
      CroppingUtils.startSquareCropping(activity, uri, Uri.fromFile(getTempFile(activity, uri)));
   }

   private File getTempFile(Context context, Uri originalUri) {
      File tempDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + TEMP_PHOTO_DIR);
      String fileName = String.valueOf(System.currentTimeMillis() + "_" + originalUri.getLastPathSegment());
      File targetFile = new File(tempDir, fileName);
      if (!targetFile.getParentFile().exists()) {
         targetFile.getParentFile().mkdirs();
      }
      return targetFile;
   }

   @Override
   public void destroy() {
      if (!croppedImagesStream.hasCompleted()) {
         croppedImagesStream.onCompleted();
      }
   }

   @Override
   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!CroppingUtils.isCroppingResult(requestCode, resultCode)) {
         return false;
      }

      Pair<String, Throwable> resultPair = CroppingUtils.obtainResults(requestCode, resultCode, data);
      if (resultPair == null) {
         return true;
      }

      onCropFinished(resultPair.first, String.valueOf(resultPair.second));
      return true;
   }

   private void onCropFinished(String path, String errorMsg) {
      if (!TextUtils.isEmpty(path)) {
         //added due to avoid result emit before view subscribes on this stream
         handler.post(() -> croppedImagesStream.onNext(Notification.createOnNext(new File(path))));
      } else {
         Timber.e("Error during cropping: %s",  errorMsg);
      }
   }
}
