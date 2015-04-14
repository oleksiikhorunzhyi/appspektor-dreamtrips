package com.worldventures.dreamtrips.modules.tripsimages.api;

import android.content.Context;
import android.util.Log;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class S3ImageUploader {

    @Inject
    @Global
    protected transient EventBus eventBus;
    @Inject
    protected transient TransferManager transferManager;
    @Inject
    protected transient Context context;
    private transient double byteTransferred;
    private transient int lastPercent;

    public String uploadImageToS3(String fileUri, String taskId) {
        File file = UploadingFileManager.copyFileIfNeed(fileUri, context);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("");

        Upload uploadHandler = null;

        try {
            uploadHandler = transferManager.upload(
                    BuildConfig.BUCKET_NAME.toLowerCase(Locale.US),
                    BuildConfig.BUCKET_ROOT_PATH + file.getName(),
                    new FileInputStream(file), metadata
            );
        } catch (FileNotFoundException e) {
            Log.e(S3ImageUploader.class.getSimpleName(), "", e);
        }

        ProgressListener progressListener = progressEvent -> {
            byteTransferred += progressEvent.getBytesTransferred();
            double l = byteTransferred / file.length() * 100;
            if ((l > lastPercent + 5 || l >= 95) && l <= 99) {
                lastPercent = (int) l;
                Log.v("Progress event", "send UploadProgressUpdateEvent:" + l);
                eventBus.post(new UploadProgressUpdateEvent(taskId, (int) l));
            }
        };

        uploadHandler.addProgressListener(progressListener);

        UploadResult uploadResult = null;

        try {
            uploadResult = uploadHandler.waitForUploadResult();
        } catch (InterruptedException e) {
            Log.e(S3ImageUploader.class.getSimpleName(), "", e);
        }

        file.delete();
        return getURLFromUploadResult(uploadResult);
    }


    private String getURLFromUploadResult(UploadResult uploadResult) {
        return "https://" + uploadResult.getBucketName() + ".s3.amazonaws.com/" + uploadResult.getKey();
    }
}
