package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

public class InsertNewImageUploadTaskEvent {


    private ImageUploadTask uploadTask;

    public InsertNewImageUploadTaskEvent(ImageUploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
