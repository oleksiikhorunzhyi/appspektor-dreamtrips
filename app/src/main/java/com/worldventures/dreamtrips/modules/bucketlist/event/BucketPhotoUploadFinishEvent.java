package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

public class BucketPhotoUploadFinishEvent {
    private BucketPhotoUploadTask task;
    private BucketPhoto bucketPhoto;

    public BucketPhotoUploadFinishEvent(BucketPhotoUploadTask task, BucketPhoto bucketPhoto) {

        this.task = task;
        this.bucketPhoto = bucketPhoto;
    }

    public BucketPhotoUploadTask getTask() {
        return task;
    }

    public BucketPhoto getBucketPhoto() {
        return bucketPhoto;
    }
}
