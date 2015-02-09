package com.worldventures.dreamtrips.utils.busevents;

public class PhotoDeletedEvent {
    private int photoId;

    public PhotoDeletedEvent(int photoId) {
        this.photoId = photoId;
    }

    public int getPhotoId() {
        return photoId;
    }
}