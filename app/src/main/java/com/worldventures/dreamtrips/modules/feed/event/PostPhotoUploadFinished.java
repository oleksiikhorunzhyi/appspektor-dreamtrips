package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

public class PostPhotoUploadFinished {

    private String taskId;
    private String originUrl;

    public PostPhotoUploadFinished(String taskId, String originUrl) {
        this.taskId = taskId;
        this.originUrl = originUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getTaskId() {
        return taskId;
    }
}
