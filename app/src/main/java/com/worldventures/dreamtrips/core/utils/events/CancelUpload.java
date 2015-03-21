package com.worldventures.dreamtrips.core.utils.events;


import com.worldventures.dreamtrips.core.uploader.ImageUploadTask;

public class CancelUpload {
    private final ImageUploadTask uploadTask;

    public CancelUpload(ImageUploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
