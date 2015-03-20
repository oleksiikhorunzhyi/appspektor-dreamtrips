package com.worldventures.dreamtrips.utils.events;


import com.worldventures.dreamtrips.core.uploader.ImageUploadTask;

public class PhotoUploadStarted {
    private ImageUploadTask uploadTask;

    public PhotoUploadStarted(ImageUploadTask uploadTask) {

        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
