package com.worldventures.dreamtrips.utils.busevents;

public class PhotoUploadFailedEvent {

    String taskId;

    public PhotoUploadFailedEvent(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
