package com.worldventures.dreamtrips.core.utils.events;

public class UploadProgressUpdateEvent {
    protected int progress;
    protected String taskId;

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
