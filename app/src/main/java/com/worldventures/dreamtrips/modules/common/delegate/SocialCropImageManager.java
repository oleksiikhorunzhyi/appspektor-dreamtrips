package com.worldventures.dreamtrips.modules.common.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;

import com.messenger.util.CroppingUtils;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.ValidationUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import rx.Notification;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class SocialCropImageManager {

   public static final int SOCIAL_CROP_REQUEST_CODE = 9923;

   private static final int RATIO_X_DEFAULT = 1;
   private static final int RATIO_Y_DEFAULT = 1;

   private static final String TEMP_PHOTO_DIR = "cropped_images";

   private Context context;

   private DreamSpiceManager dreamSpiceManager;

   private PublishSubject<Notification<File>> croppedImagesStream = PublishSubject.create();

   private int ratioX = RATIO_X_DEFAULT;
   private int ratioY = RATIO_Y_DEFAULT;

   public SocialCropImageManager(Context context, DreamSpiceManager dreamSpiceManager) {
      this.dreamSpiceManager = dreamSpiceManager;
      init(context);
   }

   private void init(Context context) {
      this.context = context;
      if (!dreamSpiceManager.isStarted()) {
         dreamSpiceManager.start(context);
      }
   }

   public void cropImage(Fragment fragment, String filePath) {
      if (context == null) {
         throw new IllegalStateException("You must call init() first");
      }
      if (ValidationUtils.isUrl(filePath)) {
         cacheFacebookImage(filePath, path -> startCropActivity(fragment, path, path));
      } else {
         executeCrop(fragment, filePath);
      }
   }

   public Observable<Notification<File>> getCroppedImagesStream() {
      return croppedImagesStream;
   }

   public void setAspectRatio(int ratioX, int ratioY) {
      this.ratioX = ratioX;
      this.ratioY = ratioY;
   }

   private void executeCrop(Fragment fragment, String originalFilePath) {
      String temporaryFile = getTempFile(originalFilePath).getAbsolutePath();
      startCropActivity(fragment, originalFilePath, temporaryFile);
   }

   private void cacheFacebookImage(String url, Action<String> action) {
      String filePath = CachedEntity.getFilePath(context, truncateUrlParams(url));
      BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(url, new File(filePath));

      dreamSpiceManager.execute(bigBinaryRequest, inputStream -> action.action(filePath), e -> reportError(e, "Could not copy avatar file from Facebook"));
   }

   private String truncateUrlParams(@NonNull String url) {
      return url.split("\\?")[0];
   }

   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!isCroppingResult(requestCode)) return false;

      Pair<String, Throwable> resultPair = obtainResults(requestCode, resultCode, data);
      if (resultPair == null) return true;

      onCropFinished(resultPair.first, String.valueOf(resultPair.second));
      return true;
   }

   private boolean isCroppingResult(int requestCode) {
      return requestCode == SOCIAL_CROP_REQUEST_CODE;
   }

   private Pair<String, Throwable> obtainResults(int requestCode, int resultCode, Intent data) {
      if (resultCode == Activity.RESULT_OK && requestCode == SOCIAL_CROP_REQUEST_CODE) {
         final Uri resultUri = UCrop.getOutput(data);
         return new Pair<>(resultUri.getPath(), null);
      } else if (resultCode == UCrop.RESULT_ERROR) {
         return new Pair<>(null, UCrop.getError(data));
      }
      return null;
   }

   private void onCropFinished(String path, String errorMsg) {
      if (!TextUtils.isEmpty(path)) {
         reportSuccess(path);
      } else {
         reportError(null, "Error during cropping: " + errorMsg);
      }
   }

   private File getTempFile(String originalFilePath) {
      File originalFile = new File(originalFilePath);
      File tempDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + TEMP_PHOTO_DIR);
      String fileName = String.valueOf(System.currentTimeMillis() + "_" + originalFile.getName());
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

   private void startCropActivity(Fragment fragment, String originalPath, String targetPath) {
      if (fragment == null) {
         Timber.w("Cannot start cropping activity, starting fragment is null");
         return;
      }
      CroppingUtils.startCropping(context, fragment, SOCIAL_CROP_REQUEST_CODE, originalPath, targetPath, ratioX, ratioY);
   }
}
