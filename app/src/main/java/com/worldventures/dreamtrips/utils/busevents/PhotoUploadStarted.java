package com.worldventures.dreamtrips.utils.busevents;


import com.worldventures.dreamtrips.core.model.ImageUploadTask;

public class PhotoUploadStarted {
    private ImageUploadTask uploadTask;

    public PhotoUploadStarted(ImageUploadTask uploadTask) {

        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
