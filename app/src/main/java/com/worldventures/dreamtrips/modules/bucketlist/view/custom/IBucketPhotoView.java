package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.util.List;

public interface IBucketPhotoView {
    void setImages(List<BucketPhoto> images);

    void addImage(UploadTask images);

    UploadTask getBucketPhotoUploadTask(long filePath);

    void addImages(List<UploadTask> tasks);

    void replace(UploadTask photoUploadTask, BucketPhoto bucketPhoto);

    void deleteImage(BucketPhoto photo);

    void deleteImage(UploadTask photo);

    void itemChanged(Object item);

}