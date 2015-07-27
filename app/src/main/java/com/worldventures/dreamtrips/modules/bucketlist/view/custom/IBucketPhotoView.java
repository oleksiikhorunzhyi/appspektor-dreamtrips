package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.membership.model.TemplatePhoto;

import java.util.List;

public interface IBucketPhotoView {
    void setImages(List<BucketPhoto> images);

    void addImage(BucketPhotoUploadTask images);

    void addImages(List<BucketPhotoUploadTask> tasks);

    void replace(BucketPhotoUploadTask photoUploadTask, BucketPhoto bucketPhoto);

    void deleteImage(BucketPhoto photo);

    void deleteImage(BucketPhotoUploadTask photo);

    void addTemplatePhoto(TemplatePhoto templatePhoto);

    void deleteAtPosition(int position);

    void showAddPhotoDialog(boolean showButton);

    void addFirstItem();

    List getImages();
}