package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public class BucketPhotoUploadCancelRequestEvent {
    private UploadTask modelObject;

    public BucketPhotoUploadCancelRequestEvent(UploadTask modelObject) {

        this.modelObject = modelObject;
    }

    public UploadTask getModelObject() {
        return modelObject;
    }
}
