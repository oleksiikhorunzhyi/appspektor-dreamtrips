package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.apptentive.android.sdk.Log;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFinished;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadStarted;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import java.net.URL;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UploadTripPhotoCommand extends DreamTripsRequest<Photo> {

    @Inject
    @Global
    protected transient EventBus eventBus;

    protected S3ImageUploader s3uploader = new S3ImageUploader();

    @Inject
    protected SnappyRepository db;

    private ImageUploadTask uploadTask;

    public UploadTripPhotoCommand(ImageUploadTask uploadTask, Injector injector) {
        super(Photo.class);
        this.uploadTask = uploadTask;
        injector.inject(this);
        injector.inject(s3uploader);
    }

    @Override
    public Photo loadDataFromNetwork() {
        try {
            eventBus.post(new PhotoUploadStarted(uploadTask));
            db.saveUploadImageTask(uploadTask);

            String fileUri = uploadTask.getFileUri();
            String taskId = uploadTask.getTaskId();

            URL urlFromUploadResult = s3uploader.uploadImageToS3(fileUri, taskId);

            uploadTask.setOriginUrl(urlFromUploadResult.toString());

            eventBus.post(new UploadProgressUpdateEvent(uploadTask.getTaskId(), 100));

            Photo photo = getService().uploadTripPhoto(uploadTask);
            photo.setTaskId(uploadTask.getTaskId());

            db.removeImageUploadTask(uploadTask);
            eventBus.post(new PhotoUploadFinished(photo));
            return photo;
        } catch (Exception e) {
            Log.e("", e);
            uploadTask.setFailed(true);
            db.saveUploadImageTask(uploadTask);

            eventBus.post(new PhotoUploadFailedEvent(uploadTask.getTaskId()));
        }
        return null;
    }

}
