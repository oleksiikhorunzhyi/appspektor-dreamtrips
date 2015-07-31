package com.worldventures.dreamtrips.modules.membership.api;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.tripsimages.api.S3ImageUploader;

import java.net.URL;

public class UploadTemplatePhotoCommand extends DreamTripsRequest<InviteTemplate> {

    private String personalMessage;

    protected S3ImageUploader s3uploader = new S3ImageUploader();

    protected BucketPhotoUploadTask photoUploadTask;


    public UploadTemplatePhotoCommand(BucketPhotoUploadTask photoUploadTask,
                                      String personalMessage,
                                      Injector injector) {
        super(InviteTemplate.class);
        this.photoUploadTask = photoUploadTask;
        this.personalMessage = personalMessage;
        injector.inject(s3uploader);
    }

    @Override
    public InviteTemplate loadDataFromNetwork() {
        String fileUri = photoUploadTask.getFilePath();
        long taskId = photoUploadTask.getTaskId();

        URL urlFromUploadResult = s3uploader.uploadImageToS3(fileUri, String.valueOf(taskId));

        return getService()
                .createInviteTemplate(photoUploadTask.getBucketId(),
                        personalMessage,
                        urlFromUploadResult.toString());
    }

}