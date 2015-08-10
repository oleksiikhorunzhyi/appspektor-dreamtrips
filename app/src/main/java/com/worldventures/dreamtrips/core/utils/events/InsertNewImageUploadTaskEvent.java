package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public class InsertNewImageUploadTaskEvent {

    private UploadTask uploadTask;

    public InsertNewImageUploadTaskEvent(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public UploadTask getUploadTask() {
        return uploadTask;
    }
}
