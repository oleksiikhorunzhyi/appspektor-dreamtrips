package com.worldventures.dreamtrips.modules.tripsimages.events;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class ImageUploadedEvent {

    public final boolean isSuccess;
    public final UploadTask task;
    public final Photo photo;

    public ImageUploadedEvent(boolean isSuccess, UploadTask task, Photo photo) {
        this.isSuccess = isSuccess;
        this.task = task;
        this.photo = photo;
    }
}
