package com.worldventures.dreamtrips.core.uploader.job;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.Constants;
import com.worldventures.dreamtrips.core.uploader.UploadingAPI;
import com.worldventures.dreamtrips.core.uploader.UploadingFileManager;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.busevents.CancelUpload;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFinished;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadStarted;
import com.worldventures.dreamtrips.utils.busevents.UploadProgressUpdateEvent;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

import static com.google.common.base.Preconditions.checkNotNull;

public class UploadJob extends Job {
    public static final String TAG = UploadJob.class.getSimpleName();

    private final String taskId;

    @Inject
    transient TransferManager transferManager;

    @Inject
    transient Context context;

    @Inject
    transient UploadingFileManager uploadingFileManager;

    @Inject
    transient UploadingAPI uploadingAPI;

    @Inject
    @Global
    transient EventBus eventBus;

    transient Upload uploadHandler;

    transient double byteTransferred;
    transient int lastPercent;

    boolean isCanceled;

    public UploadJob(String taskId) {
        super(new Params(Priority.MID).requireNetwork().setDelayMs(1000).persist());

        this.taskId = taskId;
    }

    public void onEvent(CancelUpload cancelUpload) {
        this.isCanceled = true;
        this.onCancel();
    }

    @Override
    public void onAdded() {
        Log.w(TAG, "onAdded");
        new Handler(context.getMainLooper()).post(() -> {
            Repository<ImageUploadTask> repository = new Repository<ImageUploadTask>(Realm.getInstance(context), ImageUploadTask.class);

            ImageUploadTask uploadTask = repository.query().equalTo("taskId", taskId).findFirst();

            checkNotNull(uploadTask);
            Log.w(TAG, "send eventBus: PhotoUploadStarted");

            eventBus.post(new PhotoUploadStarted(ImageUploadTask.copy(uploadTask)));
        });

    }

    @Override
    public void onRun() throws Throwable {
        Log.w(TAG, "onRun");

        if (this.isCanceled) {
            return;
        }

        Repository<ImageUploadTask> repository = new Repository<ImageUploadTask>(Realm.getInstance(context), ImageUploadTask.class);

        ImageUploadTask uploadTask = repository.query().equalTo("taskId", this.taskId).findFirst();

        checkNotNull(uploadTask);

        String taskId = uploadTask.getTaskId();
        Log.w(TAG, "task ID: " + uploadTask.getTaskId());

        File file = this.uploadingFileManager.copyFileIfNeed(uploadTask.getFileUri());

        checkNotNull(file, "Can't copy file into uploader storage");

        Upload uploadHandler = transferManager.upload(
                Constants.BUCKET_NAME.toLowerCase(Locale.US),
                Constants.BUCKET_ROOT_PATH + file.getName(),
                file
        );

        ProgressListener progressListener = progressEvent -> {
            byteTransferred += progressEvent.getBytesTransferred();
            double l = byteTransferred / file.length() * 100;
            if (l > lastPercent + 5 || l > 99) {
                lastPercent = (int) l;
                Log.i(TAG, "send Event UploadProgressUpdateEvent:" + l);

                eventBus.post(new UploadProgressUpdateEvent(taskId, (int) l));
            }
        };

        uploadHandler.addProgressListener(progressListener);

        UploadResult uploadResult = uploadHandler.waitForUploadResult();

        repository.transaction(realm -> {
            uploadTask.setOriginUrl(UploadJob.this.getURLFromUploadResult(uploadResult));
        });

        ImageUploadTask copy = ImageUploadTask.copy(uploadTask);
        Photo photo = uploadingAPI.uploadTripPhoto(copy);
        photo.setTaskId(copy.getTaskId());
        repository.remove(uploadTask);

        file.delete();
        Log.w(TAG, "send event PhotoUploadFinished");

        eventBus.post(new PhotoUploadFinished(photo));
    }

    private String getURLFromUploadResult(UploadResult uploadResult) {
        return "https://" + uploadResult.getBucketName() + ".s3.amazonaws.com/" + uploadResult.getKey();
    }

    @Override
    protected void onCancel() {
        Log.w(TAG, "onCancel");
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.w(TAG, "shouldReRunOnThrowable");
        return true;
    }
}
