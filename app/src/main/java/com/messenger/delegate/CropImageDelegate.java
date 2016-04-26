package com.messenger.delegate;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.util.CroppingUtils;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.ValidationUtils;

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
    private Handler handler = new Handler();

    private DreamSpiceManager dreamSpiceManager;

    private PublishSubject<Notification<File>> croppedImagesStream = PublishSubject.create();

    private int ratioX = RATIO_X_DEFAULT;
    private int ratioY = RATIO_Y_DEFAULT;

    public CropImageDelegate(Activity activity, DreamSpiceManager dreamSpiceManager) {
        this.dreamSpiceManager = dreamSpiceManager;
        init(activity);
    }

    private void init(Activity activity) {
        this.activity = new WeakReference<>(activity);
        context = activity.getApplicationContext();
        if (!dreamSpiceManager.isStarted()) {
            dreamSpiceManager.start(context);
        }
    }

    public void cropImage(String filePath) {
        if (activity == null) {
            throw new IllegalStateException("You must call init() first");
        }
        if (ValidationUtils.isUrl(filePath)) {
            cacheFacebookImage(filePath, path -> startCropActivity(path, path));
        } else {
            executeCrop(filePath);
        }

    }

    public Observable<Notification<File>> getCroppedImagesStream() {
        return croppedImagesStream;
    }

    public void setAspectRatio(int ratioX, int ratioY) {
        this.ratioX = ratioX;
        this.ratioY = ratioY;
    }

    private void executeCrop(String originalFilePath) {
        String temporaryFile = getTempFile(originalFilePath).getAbsolutePath();
        startCropActivity(originalFilePath, temporaryFile);
    }

    private void cacheFacebookImage(String url, Action<String> action) {
        String filePath = CachedEntity.getFilePath(context, truncateUrlParams(url));
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(url, new File(filePath));

        dreamSpiceManager.execute(bigBinaryRequest, inputStream -> action.action(filePath),
                e -> reportError(e, "Could not copy avatar file from Facebook"));
    }

    private String truncateUrlParams(@NonNull String url) {
        return url.split("\\?")[0];
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

    private File getTempFile(String originalFilePath) {
        File originalFile = new File(originalFilePath);
        File tempDir = new File(context.getCacheDir().getAbsolutePath()
                + File.separator + TEMP_PHOTO_DIR);
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

    private void startCropActivity(String originalPath, String targetPath) {
        if (activity.get() == null) {
            Timber.w("Cannot start cropping activity, starting activity is null");
            return;
        }
        CroppingUtils.startCropping(activity.get(), originalPath, targetPath, ratioX, ratioY);
    }
}
