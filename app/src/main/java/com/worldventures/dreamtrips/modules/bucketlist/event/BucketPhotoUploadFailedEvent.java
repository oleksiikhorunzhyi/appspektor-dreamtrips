package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketPhotoUploadFailedEvent {
    private long taskId;

    public BucketPhotoUploadFailedEvent(long bucketId) {

        this.taskId = bucketId;
    }

    public long getTaskId() {
        return taskId;
    }
}
