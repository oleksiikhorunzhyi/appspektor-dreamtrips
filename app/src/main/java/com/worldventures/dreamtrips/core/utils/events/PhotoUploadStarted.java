package com.worldventures.dreamtrips.core.utils.events;


import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

public class PhotoUploadStarted {
    private ImageUploadTask uploadTask;

    public PhotoUploadStarted(ImageUploadTask uploadTask) {

        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
