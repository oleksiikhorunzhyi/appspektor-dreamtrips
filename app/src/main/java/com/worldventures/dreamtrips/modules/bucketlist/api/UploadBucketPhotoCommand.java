package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.apptentive.android.sdk.Log;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFailedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFinished;
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
            int taskId = photoUploadTask.getBucketId();

            String urlFromUploadResult = s3uploader.uploadImageToS3(fileUri, String.valueOf(taskId));
            BucketPhoto bucketPhoto = new BucketPhoto();
            bucketPhoto.setBucketId(photoUploadTask.getBucketId());
            bucketPhoto.setUrl(urlFromUploadResult);

            eventBus.post(new UploadProgressUpdateEvent(String.valueOf(taskId), 100));

            BucketPhoto photo = getService().uploadBucketPhoto(photoUploadTask.getBucketId(), bucketPhoto);
            photo.setBucketId(photoUploadTask.getBucketId());

            db.removeBucketPhotoTask(photoUploadTask);
            eventBus.post(new BucketPhotoUploadFinished(photo));

            return photo;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e);
            eventBus.post(new BucketPhotoUploadFailedEvent(photoUploadTask.getBucketId()));
        }
        return null;
    }

}
