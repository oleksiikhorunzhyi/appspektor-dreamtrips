package com.messenger.ui.util.avatar;


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

import io.techery.scalablecropp.library.Crop;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class CropImageDelegate {

    private static final int RATIO_X_DEFAULT = 1;
    private static final int RATIO_Y_DEFAULT = 1;

    private static final String TEMP_PHOTO_FILE_PREFIX = "temp_copy_of_";

    private Activity activity;
    private Context context;

    private DreamSpiceManager dreamSpiceManager;

    private PublishSubject<File> croppedImagesStream = PublishSubject.create();

    private int ratioX = RATIO_X_DEFAULT;
    private int ratioY = RATIO_Y_DEFAULT;

    public CropImageDelegate(DreamSpiceManager dreamSpiceManager) {
        this.dreamSpiceManager = dreamSpiceManager;
    }

    public void init(Activity activity) {
        this.activity = activity;
        context = activity.getApplicationContext();
        dreamSpiceManager.start(context);
    }

    public void cropImage(ChosenImage image) {
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
        File originalFile = new File(originalFilePath);
        dreamSpiceManager.execute(new CopyFileTask(originalFile,
                        originalFile.getParentFile() + "/" + TEMP_PHOTO_FILE_PREFIX + originalFile.getName()),
                path -> startCropActivity(path),
                e -> reportErrorToImagesStream(e, "Could not copy avatar file from disk"));
    }

    private void cacheFacebookImage(String url, Action<String> action) {
        String filePath = CachedEntity.getFilePath(context, CachedEntity.getFilePath(context, url));
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(url, new File(filePath));

        dreamSpiceManager.execute(bigBinaryRequest, inputStream -> action.action(filePath),
                e -> reportErrorToImagesStream(e, "Could not copy avatar file from Facebook"));
    }

    public void onCropFinished(String path, String errorMsg) {
        if (!TextUtils.isEmpty(path)) {
            croppedImagesStream.onNext(new File(path));
        } else {
            reportErrorToImagesStream(null, "Error during cropping: " + errorMsg);
        }
    }

    private void reportErrorToImagesStream(Throwable originalException, String message) {
        if (originalException == null) {
            Timber.e(message);
            croppedImagesStream.onError(new RuntimeException(message));
        } else {
            Timber.e(originalException, message);
            croppedImagesStream.onError(new RuntimeException(message, originalException));
        }
    }

    private void startCropActivity(String path) {
        Crop.prepare(path).ratio(ratioX, ratioY).startFrom(activity);
    }
}
