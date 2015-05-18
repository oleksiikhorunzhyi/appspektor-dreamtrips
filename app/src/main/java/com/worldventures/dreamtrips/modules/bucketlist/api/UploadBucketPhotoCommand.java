package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.apptentive.android.sdk.Log;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
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

            BucketPhoto uploadObject = getUploadObject(urlFromUploadResult);

            BucketPhoto photo = null;
            if (isCancelled()) {
                eventBus.post(new BucketPhotoUploadCancelEvent(photoUploadTask));
            } else {
                photo = getService().uploadBucketPhoto(photoUploadTask.getBucketId(), uploadObject);
            }
            eventBus.post(new UploadProgressUpdateEvent(String.valueOf(taskId), 100));
            db.removeBucketPhotoTask(photoUploadTask);

            return photo;

        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e);
            eventBus.post(new BucketPhotoUploadFailedEvent(photoUploadTask.getTaskId()));
        }

        return null;
    }

    private BucketPhoto getUploadObject(String urlFromUploadResult) {
        BucketPhoto bucketPhoto = new BucketPhoto();
        bucketPhoto.setOriginUrl(urlFromUploadResult);
        return bucketPhoto;
    }

}
