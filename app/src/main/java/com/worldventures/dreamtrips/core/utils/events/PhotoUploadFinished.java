package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.core.model.Photo;

public class PhotoUploadFinished {
    private Photo photo;

    public PhotoUploadFinished(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }
}