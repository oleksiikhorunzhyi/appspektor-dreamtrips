package com.worldventures.dreamtrips.modules.tripsimages.events;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public class UploadStatusChanged {

    private UploadTask uploadTask;

    public UploadStatusChanged(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public UploadTask getUploadTask() {
        return uploadTask;
    }
}
