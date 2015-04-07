package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

public class BucketPhotoReuploadRequestEvent {

    private BucketPhotoUploadTask task;

    public BucketPhotoReuploadRequestEvent(BucketPhotoUploadTask task) {
        this.task = task;
    }

    public BucketPhotoUploadTask getTask() {
        return task;
    }

}
