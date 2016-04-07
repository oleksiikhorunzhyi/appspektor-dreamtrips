package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public class UploadBucketPhotoCommand extends DreamTripsRequest<BucketPhoto> {


    protected UploadTask photoUploadTask;
    private String bucketItemId;

    public UploadBucketPhotoCommand(String bucketItemId, UploadTask photoUploadTask) {
        super(BucketPhoto.class);
        this.bucketItemId = bucketItemId;
        this.photoUploadTask = photoUploadTask;
    }

    @Override
    public BucketPhoto loadDataFromNetwork() {
        BucketPhoto uploadObject = getUploadObject(photoUploadTask.getOriginUrl());

        return getService().uploadBucketPhoto(bucketItemId, uploadObject);
    }

    private BucketPhoto getUploadObject(String urlFromUploadResult) {
        BucketPhoto bucketPhoto = new BucketPhoto();
        bucketPhoto.setOriginUrl(urlFromUploadResult);
        return bucketPhoto;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_upload_bl_photo;
    }

}