package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

public class BucketPhotoAsCoverRequestEvent {
    private BucketPhoto photo;

    public BucketPhotoAsCoverRequestEvent(BucketPhoto bucketPhoto) {
        this.photo = bucketPhoto;
    }

    public BucketPhoto getPhoto() {
        return photo;
    }
}
