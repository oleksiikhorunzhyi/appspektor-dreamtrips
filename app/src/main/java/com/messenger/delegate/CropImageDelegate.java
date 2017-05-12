package com.messenger.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.badoo.mobile.util.WeakHandler;
import com.messenger.util.CroppingUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import rx.Notification;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class CropImageDelegate {

   private static final int RATIO_X_DEFAULT = 1;
   private static final int RATIO_Y_DEFAULT = 1;

   private static final String TEMP_PHOTO_DIR = "cropped_images";

   private WeakReference<Activity> activity;
   private Context context;
   private WeakHandler handler = new WeakHandler();

   private PublishSubject<Notification<File>> croppedImagesStream = PublishSubject.create();

   public CropImageDelegate(Activity activity) {
      init(activity);
   }

   private void init(Activity activity) {
      this.activity = new WeakReference<>(activity);
      context = activity.getApplicationContext();
   }

   @Deprecated
   public void cropImage(String filePath) {
      cropImage(Uri.parse(filePath));
   }

   public void cropImage(Uri uri) {
      if (activity == null) {
         throw new IllegalStateException("You must call init() first");
      }
      startCropActivity(uri, Uri.fromFile(getTempFile(uri)));
   }

   public Observable<Notification<File>> getCroppedImagesStream() {
      return croppedImagesStream;
   }

   public void destroy() {
      if (croppedImagesStream != null && !croppedImagesStream.hasCompleted()) croppedImagesStream.onCompleted();
   }

   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!CroppingUtils.isCroppingResult(requestCode, resultCode)) return false;

      Pair<String, Throwable> resultPair = CroppingUtils.obtainResults(requestCode, resultCode, data);
      if (resultPair == null) return true;

      onCropFinished(resultPair.first, String.valueOf(resultPair.second));
      return true;
   }

   private void onCropFinished(String path, String errorMsg) {
      if (!TextUtils.isEmpty(path)) {
         // TODO Improve this. Workaround for onAttachedToWindow() called after
         // onActivityResult() after user rotated screen in crop activity
         handler.post(() -> reportSuccess(path));
      } else {
         reportError(null, "Error during cropping: " + errorMsg);
      }
   }

   private File getTempFile(Uri originalUri) {
      File tempDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + TEMP_PHOTO_DIR);
      String fileName = String.valueOf(System.currentTimeMillis() + "_" + originalUri.getLastPathSegment());
      File targetFile = new File(tempDir, fileName);
      if (!targetFile.getParentFile().exists()) {
         targetFile.getParentFile().mkdirs();
      }
      return targetFile;
   }

   private void reportSuccess(String path) {
      croppedImagesStream.onNext(Notification.createOnNext(new File(path)));
   }

   private void reportError(Throwable originalException, String message) {
      Exception exception;
      if (originalException == null) {
         Timber.e(message);
         exception = new RuntimeException(message);
      } else {
         Timber.e(originalException, message);
         exception = new RuntimeException(message, originalException);
      }
      croppedImagesStream.onNext(Notification.createOnError(exception));
   }

   private void startCropActivity(Uri originalUri, Uri targetUri) {
      if (activity.get() == null) {
         Timber.w("Cannot start cropping activity, starting activity is null");
         return;
      }
      CroppingUtils.startCropping(activity.get(), originalUri, targetUri, RATIO_X_DEFAULT, RATIO_Y_DEFAULT);
   }
}
