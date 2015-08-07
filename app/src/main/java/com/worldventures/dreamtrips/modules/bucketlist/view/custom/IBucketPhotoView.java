package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.membership.model.TemplatePhoto;

import java.util.List;

public interface IBucketPhotoView {
    void setImages(List<BucketPhoto> images);

    void addImage(UploadTask images);

    UploadTask getBucketPhotoUploadTask(String filePath);

    void addImages(List<UploadTask> tasks);

    void replace(UploadTask photoUploadTask, BucketPhoto bucketPhoto);

    void deleteImage(BucketPhoto photo);

    void deleteImage(UploadTask photo);

    void deleteAtPosition(int position);

    void showAddPhotoDialog(boolean showButton);

    void addFirstItem();

    void itemChanged(Object item);

    List getImages();
}