package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

public class UploadBucketPhotoCommand extends DreamTripsRequest<BucketPhoto> {


    protected BucketPhotoUploadTask photoUploadTask;

    public UploadBucketPhotoCommand(BucketPhotoUploadTask photoUploadTask) {
        super(BucketPhoto.class);
        this.photoUploadTask = photoUploadTask;
    }

    @Override
    public BucketPhoto loadDataFromNetwork() {
        BucketPhoto uploadObject = getUploadObject(photoUploadTask.getAmazonResultUrl());

        return getService().uploadBucketPhoto(photoUploadTask.getBucketId(), uploadObject);
    }


    private BucketPhoto getUploadObject(String urlFromUploadResult) {
        BucketPhoto bucketPhoto = new BucketPhoto();
        bucketPhoto.setOriginUrl(urlFromUploadResult);
        return bucketPhoto;
    }

}