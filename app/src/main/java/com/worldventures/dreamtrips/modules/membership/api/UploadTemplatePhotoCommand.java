package com.worldventures.dreamtrips.modules.membership.api;

import android.util.Log;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.tripsimages.api.S3ImageUploader;

public class UploadTemplatePhotoCommand extends DreamTripsRequest<InviteTemplate> {

    private String personalMessage;

    protected S3ImageUploader s3uploader = new S3ImageUploader();

    protected BucketPhotoUploadTask photoUploadTask;


    public UploadTemplatePhotoCommand(BucketPhotoUploadTask photoUploadTask, String personalMessage) {
        super(InviteTemplate.class);
        this.photoUploadTask = photoUploadTask;
        this.personalMessage = personalMessage;
    }

    @Override
    public InviteTemplate loadDataFromNetwork() {
        try {
            String fileUri = photoUploadTask.getFilePath();
            int taskId = photoUploadTask.getTaskId();

            String urlFromUploadResult = s3uploader.uploadImageToS3(fileUri, String.valueOf(taskId));

            InviteTemplate template = getService()
                    .createInviteTemplate(photoUploadTask.getBucketId(),
                            personalMessage,
                            urlFromUploadResult);

            return template;
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }

        return null;
    }

}