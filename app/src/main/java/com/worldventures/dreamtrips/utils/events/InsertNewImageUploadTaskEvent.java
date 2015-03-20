package com.worldventures.dreamtrips.utils.events;

import com.worldventures.dreamtrips.core.uploader.ImageUploadTask;

public class InsertNewImageUploadTaskEvent {


    private ImageUploadTask uploadTask;

    public InsertNewImageUploadTaskEvent(ImageUploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
