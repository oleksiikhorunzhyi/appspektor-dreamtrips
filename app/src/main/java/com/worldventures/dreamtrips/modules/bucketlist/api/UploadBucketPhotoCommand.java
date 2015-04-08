package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.apptentive.android.sdk.Log;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFailedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadStarted;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.api.S3ImageUploader;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UploadBucketPhotoCommand extends DreamTripsRequest<BucketPhoto> {

    @Inject
    @Global
    protected transient EventBus eventBus;

    @Inject
    protected SnappyRepository db;

    protected S3ImageUploader s3uploader = new S3ImageUploader();

    protected BucketPhotoUploadTask photoUploadTask;


    public UploadBucketPhotoCommand(BucketPhotoUploadTask photoUploadTask, Injector injector) {
        super(BucketPhoto.class);
        this.photoUploadTask = photoUploadTask;
        injector.inject(this);
        injector.inject(s3uploader);
    }

    @Override
    public BucketPhoto loadDataFromNetwork() {
        try {
            eventBus.post(new BucketPhotoUploadStarted(photoUploadTask));
            db.saveBucketPhotoTask(photoUploadTask);

            String fileUri = photoUploadTask.getFilePath();
            int taskId = photoUploadTask.getTaskId();

            String urlFromUploadResult = s3uploader.uploadImageToS3(fileUri, String.valueOf(taskId));

            BucketPhoto uploadObject = getUploadObject(taskId, urlFromUploadResult);

            BucketPhoto photo = null;
            if (isCancelled()) {
                eventBus.post(new BucketPhotoUploadCancelEvent(photoUploadTask));
            } else {
                photo = getService().uploadBucketPhoto(photoUploadTask.getBucketId(), uploadObject);
                photo.setTaskId(taskId);
            }
            db.removeBucketPhotoTask(photoUploadTask);

            return photo;

        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e);
            eventBus.post(new BucketPhotoUploadFailedEvent(photoUploadTask.getTaskId()));
        }

        return null;
    }

    private BucketPhoto getUploadObject(int taskId, String urlFromUploadResult) {
        BucketPhoto bucketPhoto = new BucketPhoto();
        bucketPhoto.setTaskId(taskId);
        bucketPhoto.setOriginUrl(urlFromUploadResult);
        return bucketPhoto;
    }

}
