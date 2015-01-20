package com.worldventures.dreamtrips.utils.busevents;

public class UploadProgressUpdateEvent {
    int progress;
    String taskId;

    public String getTaskId() {
        return taskId;
    }

    public UploadProgressUpdateEvent(String taskId, int progress) {
        this.taskId = taskId;
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
