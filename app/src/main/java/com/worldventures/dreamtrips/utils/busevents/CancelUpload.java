package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.ImageUploadTask;

public class CancelUpload {
    private final ImageUploadTask uploadTask;

    public CancelUpload(ImageUploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
