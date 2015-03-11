package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

public class InsertNewImageUploadTaskEvent {


    private ImageUploadTask uploadTask;

    public InsertNewImageUploadTaskEvent(ImageUploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public ImageUploadTask getUploadTask() {
        return uploadTask;
    }
}
