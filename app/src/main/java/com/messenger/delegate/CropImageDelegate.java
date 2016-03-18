package com.messenger.delegate;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.CopyFileTask;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import io.techery.scalablecropp.library.Crop;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class CropImageDelegate {

    private static final int RATIO_X_DEFAULT = 1;
    private static final int RATIO_Y_DEFAULT = 1;

    private static final String TEMP_PHOTO_DIR = "cropped_images";

    private WeakReference<Activity> activity;
    private Context context;

    private DreamSpiceManager dreamSpiceManager;

    private PublishSubject<File> croppedImagesStream = PublishSubject.create();

    private int ratioX = RATIO_X_DEFAULT;
    private int ratioY = RATIO_Y_DEFAULT;

    public CropImageDelegate(DreamSpiceManager dreamSpiceManager) {
        this.dreamSpiceManager = dreamSpiceManager;
    }

    public void init(Activity activity) {
        this.activity = new WeakReference<>(activity);
        context = activity.getApplicationContext();
        if (!dreamSpiceManager.isStarted()) {
            dreamSpiceManager.start(context);
        }
    }

    public void cropImage(ChosenImage image) {
        if (activity == null) {
            throw new IllegalStateException("You must call init() first");
        }
        if (image != null) {
            String filePath = image.getFilePathOriginal();
            if (ValidationUtils.isUrl(filePath)) {
                cacheFacebookImage(filePath, path -> startCropActivity(path));
            } else {
                executeCrop(filePath);
            }
        }
    }

    public Observable<File> getCroppedImagesStream() {
        return croppedImagesStream;
    }

    public void setAspectRatio(int ratioX, int ratioY) {
        this.ratioX = ratioX;
        this.ratioY = ratioY;
    }

    /**
     * Crop library needs temp file for processing
     *
     * @param originalFilePath
     */
    private void executeCrop(String originalFilePath) {
        dreamSpiceManager.execute(new CopyFileTask(new File(originalFilePath),
                getTempFile(originalFilePath).getAbsolutePath()),
                path -> startCropActivity(path),
                e -> reportError(e, "Could not copy avatar file from disk"));
    }

    private void cacheFacebookImage(String url, Action<String> action) {
        String filePath = CachedEntity.getFilePath(context, CachedEntity.getFilePath(context, url));
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(url, new File(filePath));

        dreamSpiceManager.execute(bigBinaryRequest, inputStream -> action.action(filePath),
                e -> reportError(e, "Could not copy avatar file from Facebook"));
    }

    /*
     * Called from onActivityResult()
     */
    public void onCropFinished(String path, String errorMsg) {
        if (!TextUtils.isEmpty(path)) {
            reportSuccess(path);
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
        croppedImagesStream.onNext(new File(path));
    }

    private void reportError(Throwable originalException, String message) {
        if (originalException == null) {
            Timber.e(message);
            croppedImagesStream.onError(new RuntimeException(message));
        } else {
            Timber.e(originalException, message);
            croppedImagesStream.onError(new RuntimeException(message, originalException));
        }
    }

    private void startCropActivity(String path) {
        if (activity.get() == null) {
            Timber.w("Cannot start cropping activity, starting activity is null");
            return;
        }
        Crop.prepare(path).ratio(ratioX, ratioY).startFrom(activity.get());
    }
}
