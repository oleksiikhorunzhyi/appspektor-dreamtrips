package com.worldventures.dreamtrips.utils.events;

public class UploadProgressUpdateEvent {
    int progress;
    String taskId;

    public UploadProgressUpdateEvent(String taskId, int progress) {
        this.taskId = taskId;
        this.progress = progress;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getProgress() {
        return progress;
    }
}
