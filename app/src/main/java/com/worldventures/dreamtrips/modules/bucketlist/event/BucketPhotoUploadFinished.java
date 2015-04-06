package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

public class BucketPhotoUploadFinished {
    private BucketPhoto photo;

    public BucketPhotoUploadFinished(BucketPhoto photo) {

        this.photo = photo;
    }

    public BucketPhoto getPhoto() {
        return photo;
    }
}
