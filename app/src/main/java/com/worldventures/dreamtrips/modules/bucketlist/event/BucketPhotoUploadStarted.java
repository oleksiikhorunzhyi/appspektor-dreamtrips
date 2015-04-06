package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

public class BucketPhotoUploadStarted {
    private BucketPhotoUploadTask bucketPhoto;

    public BucketPhotoUploadStarted(BucketPhotoUploadTask bucketPhoto) {

        this.bucketPhoto = bucketPhoto;
    }

    public BucketPhotoUploadTask getBucketPhoto() {
        return bucketPhoto;
    }
}
