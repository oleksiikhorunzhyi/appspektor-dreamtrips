package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

import java.util.List;

public interface IBucketPhotoView {
    void addImages(List<BucketPhoto> images);

    void addImage(BucketPhotoUploadTask images);

    void replace(BucketPhotoUploadTask photoUploadTask, BucketPhoto bucketPhoto);

    void deleteImage(BucketPhoto photo);

    void deleteImage(BucketPhotoUploadTask photo);

    void showAddPhotoDialog();
}