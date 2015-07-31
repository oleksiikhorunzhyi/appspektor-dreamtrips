package com.worldventures.dreamtrips.modules.feed.api;

import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.tripsimages.api.S3ImageUploader;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import java.net.URL;

public class UploadPostPhotoCommand extends SpiceRequest<URL> {

    protected S3ImageUploader s3uploader = new S3ImageUploader();

    private ImageUploadTask uploadTask;

    public UploadPostPhotoCommand(ImageUploadTask uploadTask, Injector injector) {
        super(URL.class);
        this.uploadTask = uploadTask;
        injector.inject(s3uploader);
    }

    @Override
    public URL loadDataFromNetwork() {
        String fileUri = uploadTask.getFileUri();
        String taskId = uploadTask.getTaskId();
        s3uploader.setProgressListener(uploadTask::setProgress);

        return s3uploader.uploadImageToS3(fileUri, taskId);
    }

}
