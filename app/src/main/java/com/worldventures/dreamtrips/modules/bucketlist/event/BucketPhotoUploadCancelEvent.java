package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

public class BucketPhotoUploadCancelEvent {
    private BucketPhotoUploadTask photoUploadTask;

    public BucketPhotoUploadCancelEvent(BucketPhotoUploadTask photoUploadTask) {
        this.photoUploadTask = photoUploadTask;
    }

    public BucketPhotoUploadTask getTask() {
        return photoUploadTask;
    }
}
