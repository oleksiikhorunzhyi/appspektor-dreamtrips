package com.worldventures.dreamtrips.modules.common.api;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public class CancelUploadCommand extends Command<Void> {

    UploadTask uploadTask;

    AmazonS3 amazonS3;
    SnappyRepository db;

    public CancelUploadCommand(AmazonS3 amazonS3, SnappyRepository db, UploadTask uploadTask) {
        super(Void.class);
        this.uploadTask = uploadTask;
        this.amazonS3 = amazonS3;
        this.db = db;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        uploadTask.setStatus(UploadTask.Status.FAILED);
        db.removeUploadTask(uploadTask);
        amazonS3.abortMultipartUpload(new AbortMultipartUploadRequest(
                uploadTask.getBucketName(), uploadTask.getKey(), uploadTask.getAmazonTaskId()));
        return null;
    }
}
