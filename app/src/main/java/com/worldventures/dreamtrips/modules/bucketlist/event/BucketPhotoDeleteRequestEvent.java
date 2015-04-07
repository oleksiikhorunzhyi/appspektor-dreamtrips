package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

public class BucketPhotoDeleteRequestEvent {
    private BucketPhoto photo;

    public BucketPhotoDeleteRequestEvent(BucketPhoto photo) {

        this.photo = photo;
    }

    public BucketPhoto getPhoto() {
        return photo;
    }
}
