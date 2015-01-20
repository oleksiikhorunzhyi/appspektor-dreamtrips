package com.worldventures.dreamtrips.core.uploader.job;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.techery.spares.module.Annotations.Application;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.Constants;
import com.worldventures.dreamtrips.core.uploader.UploadingAPI;
import com.worldventures.dreamtrips.core.uploader.UploadingFileManager;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.busevents.UploadProgressUpdateEvent;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

import static com.google.common.base.Preconditions.checkNotNull;

public class UploadJob extends Job {
    private static final String TAG = UploadJob.class.getSimpleName();

    private final String taskId;

    @Inject
    transient TransferManager transferManager;

    @Inject
    @Application
    transient Context context;

    @Inject
    transient UploadingFileManager uploadingFileManager;

    @Inject
    transient UploadingAPI uploadingAPI;

    @Inject
    @Global
    transient EventBus eventBus;

    transient Upload uploadHandler;

    public UploadJob(String taskId) {
        super(new Params(Priority.MID).requireNetwork().setDelayMs(300).persist());

        this.taskId = taskId;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Repository<ImageUploadTask> repository = new Repository<ImageUploadTask>(Realm.getInstance(context), ImageUploadTask.class);

        ImageUploadTask uploadTask = repository.query().equalTo("taskId", this.taskId).findFirst();

        checkNotNull(uploadTask);

        String taskId = uploadTask.getTaskId();

        File file = this.uploadingFileManager.copyFileIfNeed(uploadTask.getFileUri());

        checkNotNull(file, "Can't copy file into uploader storage");

        Upload uploadHandler = transferManager.upload(
                Constants.BUCKET_NAME.toLowerCase(Locale.US),
                Constants.BUCKET_ROOT_PATH + file.getName(),
                file
        );
        Handler mainHandler = new Handler(context.getMainLooper(), new Handler.Callback() {
            double byteTransferred;
            int lastPercent;

            @Override
            public boolean handleMessage(Message msg) {
                byteTransferred += msg.what;
                double l = byteTransferred / file.length() * 100;
                if (l > lastPercent + 5 || l > 99) {
                    lastPercent = (int) l;
                    eventBus.post(new UploadProgressUpdateEvent(taskId, (int) l));
                    Log.d(TAG, "Progress:" + msg.what);
                    Log.d(TAG, "--------:" + l);
                }
                return false;
            }
        });

        ProgressListener progressListener = new ProgressListener() {

            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                mainHandler.sendEmptyMessage((int) progressEvent.getBytesTransferred());
            }
        };

        uploadHandler.addProgressListener(progressListener);

        UploadResult uploadResult = uploadHandler.waitForUploadResult();

        repository.transaction(new Repository.TransactionCallback() {
            @Override
            public void consume(Realm realm) {
                uploadTask.setOriginUrl(UploadJob.this.getURLFromUploadResult(uploadResult));
            }
        });

        Photo photo = uploadingAPI.uploadTripPhoto(ImageUploadTask.copy(uploadTask));

        repository.remove(uploadTask);

        file.delete();
    }

    private String getURLFromUploadResult(UploadResult uploadResult) {
        return "https://" + uploadResult.getBucketName() + ".s3.amazonaws.com/" + uploadResult.getKey();
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
