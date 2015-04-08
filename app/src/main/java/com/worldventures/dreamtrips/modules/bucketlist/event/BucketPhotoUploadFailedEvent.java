package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketPhotoUploadFailedEvent {
    private int taskId;

    public BucketPhotoUploadFailedEvent(int bucketId) {

        this.taskId = bucketId;
    }

    public int getTaskId() {
        return taskId;
    }
}
