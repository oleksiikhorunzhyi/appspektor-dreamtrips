package com.worldventures.dreamtrips.core.utils.events;

public class PhotoUploadFailedEvent {

    protected String taskId;

    public PhotoUploadFailedEvent(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
