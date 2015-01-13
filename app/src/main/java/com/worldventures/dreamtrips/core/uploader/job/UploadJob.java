package com.worldventures.dreamtrips.core.uploader.job;

import android.content.Context;
import android.util.Log;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.worldventures.dreamtrips.core.uploader.Constants;
import com.worldventures.dreamtrips.core.uploader.UploadingFileManager;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class UploadJob extends Job {
    private static final String TAG = UploadJob.class.getSimpleName();

    private final String filePath;

    @Inject
    transient TransferManager transferManager;

    @Inject
    transient Context context;

    @Inject
    transient UploadingFileManager uploadingFileManager;

    transient Upload uploadHandler;

    public UploadJob(String filePath) {
        super(new Params(Priority.MID).requireNetwork().persist());

        this.filePath = filePath;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Added");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Running");

        File file = this.uploadingFileManager.copyFileIfNeed(this.filePath);

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

        processUploadResults(uploadResult);

        file.delete();

        Log.d(TAG, "Uploaded:" + uploadResult.getKey());
    }

    protected void processUploadResults(UploadResult uploadResult) {

    }

    @Override
    protected void onCancel() {
        if (uploadHandler != null) {
            uploadHandler.abort();
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }
}
