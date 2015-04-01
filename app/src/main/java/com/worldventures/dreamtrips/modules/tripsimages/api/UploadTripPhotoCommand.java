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
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFinished;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadStarted;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UploadTripPhotoCommand extends DreamTripsRequest<Photo> {

    @Inject
    protected transient TransferManager transferManager;
    @Inject
    @Global
    protected transient EventBus eventBus;
    @Inject
    protected transient Context context;
    @Inject
    protected SnappyRepository db;

    private transient double byteTransferred;
    private transient int lastPercent;
    private ImageUploadTask uploadTask;

    public UploadTripPhotoCommand(ImageUploadTask uploadTask) {
        super(Photo.class);
        this.uploadTask = uploadTask;
    }

    @Override
    public Photo loadDataFromNetwork() {
        try {
            eventBus.post(new PhotoUploadStarted(uploadTask));

            File file = UploadingFileManager.copyFileIfNeed(uploadTask.getFileUri(), context);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("");
            Upload uploadHandler = transferManager.upload(
                    BuildConfig.BUCKET_NAME.toLowerCase(Locale.US),
                    BuildConfig.BUCKET_ROOT_PATH + file.getName(),
                    new FileInputStream(file), metadata
            );

            ProgressListener progressListener = progressEvent -> {
                byteTransferred += progressEvent.getBytesTransferred();
                double l = byteTransferred / file.length() * 100;
                if (l > lastPercent + 5 || l > 99) {
                    lastPercent = (int) l;
                    Log.v("Progress event", "send UploadProgressUpdateEvent:" + l);
                    eventBus.post(new UploadProgressUpdateEvent(uploadTask.getTaskId(), (int) l));
                }
            };

            uploadHandler.addProgressListener(progressListener);

            UploadResult uploadResult = null;

            uploadResult = uploadHandler.waitForUploadResult();


            uploadTask.setOriginUrl(getURLFromUploadResult(uploadResult));

            eventBus.post(new UploadProgressUpdateEvent(uploadTask.getTaskId(), 100));

            db.removeImageUploadTask(uploadTask);

            Photo photo = getService().uploadTripPhoto(uploadTask);
            photo.setTaskId(uploadTask.getTaskId());
            eventBus.post(new PhotoUploadFinished(photo));
            return photo;
        } catch (Exception e) {
            eventBus.post(new PhotoUploadFailedEvent(uploadTask.getTaskId()));
        }
        return null;
    }

    private String getURLFromUploadResult(UploadResult uploadResult) {
        return "https://" + uploadResult.getBucketName() + ".s3.amazonaws.com/" + uploadResult.getKey();
    }
}
