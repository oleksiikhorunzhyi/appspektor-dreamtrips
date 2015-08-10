package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public class BucketPhotoReuploadRequestEvent {

    private UploadTask task;

    public BucketPhotoReuploadRequestEvent(UploadTask task) {
        this.task = task;
    }

    public UploadTask getTask() {
        return task;
    }

}
