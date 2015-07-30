package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

public class PostPhotoUploadFailed {
    private String taskId;

    public PostPhotoUploadFailed(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
