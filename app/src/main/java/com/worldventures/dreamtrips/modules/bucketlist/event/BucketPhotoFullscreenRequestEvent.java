package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

public class BucketPhotoFullscreenRequestEvent {
    private BucketPhoto photo;

    public BucketPhotoFullscreenRequestEvent(BucketPhoto photo) {
        this.photo = photo;
    }

    public BucketPhoto getPhoto() {
        return photo;
    }
}
