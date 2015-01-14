package com.worldventures.dreamtrips.core.uploader.job;

import android.content.Context;
import android.util.Log;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.Constants;
import com.worldventures.dreamtrips.core.uploader.UploadingAPI;
import com.worldventures.dreamtrips.core.uploader.UploadingFileManager;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class UploadJob extends Job {
    private static final String TAG = UploadJob.class.getSimpleName();

    private final String taskId;

    @Inject
    transient TransferManager transferManager;

    @Inject
    transient Context context;

    @Inject
    transient UploadingFileManager uploadingFileManager;

    @Inject
    transient Repository<ImageUploadTask> repository;

    @Inject
    transient UploadingAPI uploadingAPI;

    transient Upload uploadHandler;


    public UploadJob(String taskId) {
        super(new Params(Priority.MID).requireNetwork().persist());

        this.taskId = taskId;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        ImageUploadTask uploadTask = repository.query().equalTo("taskId", this.taskId).findFirst();

        File file = this.uploadingFileManager.copyFileIfNeed(uploadTask.getFilePath());

        checkNotNull(file, "Can't copy file into uploader storage");

        Upload uploadHandler = transferManager.upload(
                Constants.BUCKET_NAME.toLowerCase(Locale.US),
                Constants.BUCKET_ROOT_PATH + file.getName(),
                file
        );

        ProgressListener progressListener = progressEvent -> {
            Log.d(TAG, "Progress:" + progressEvent.getBytesTransferred());
        };

        uploadHandler.addProgressListener(progressListener);

        UploadResult uploadResult = uploadHandler.waitForUploadResult();

        repository.transaction((realm) -> {
            uploadTask.setOriginPhotoURL(getURLFromUploadResult(uploadResult));
        });

        Photo photo = uploadingAPI.uploadTripPhoto(uploadTask);

        repository.remove(uploadTask);

        file.delete();
    }

    private String getURLFromUploadResult(UploadResult uploadResult) {
        return "https://" + uploadResult.getBucketName() + "s3.amazonaws.com/" + uploadResult.getKey();
    }

    @Override
    protected void onCancel() {
        if (uploadHandler != null) {
            uploadHandler.abort();
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return getCurrentRunCount() < 3;
    }
}
