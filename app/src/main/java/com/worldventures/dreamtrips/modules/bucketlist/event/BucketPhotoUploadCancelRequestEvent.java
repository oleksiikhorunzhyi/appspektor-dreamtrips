package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

public class BucketPhotoUploadCancelRequestEvent {
    private BucketPhotoUploadTask modelObject;

    public BucketPhotoUploadCancelRequestEvent(BucketPhotoUploadTask modelObject) {

        this.modelObject = modelObject;
    }

    public BucketPhotoUploadTask getModelObject() {
        return modelObject;
    }
}
