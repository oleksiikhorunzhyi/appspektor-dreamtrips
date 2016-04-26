package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoCreationItem;

import java.util.List;

public interface IBucketPhotoView {
    void setImages(List<BucketPhoto> images);

    void addImageToStart(BucketPhotoCreationItem images);

    BucketPhotoCreationItem getBucketPhotoUploadTask(String filePath);

    void replace(BucketPhotoCreationItem photoUploadTask, BucketPhoto bucketPhoto);

    void deleteImage(BucketPhoto photo);

    void deleteImage(BucketPhotoCreationItem photo);

    void itemChanged(Object item);

}