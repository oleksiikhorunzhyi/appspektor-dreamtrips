package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;

import java.util.List;

public interface BucketItemEditPresenterView extends BucketDetailsBasePresenter.View {

        void showError();

        void setCategory(int selection);

        void setCategoryItems(List<CategoryItem> items);

        CategoryItem getSelectedItem();

        boolean getStatus();

        String getTags();

        String getPeople();

        String getTitle();

        String getDescription();

        void addImages(List<BucketPhoto> images);

        void addImage(BucketPhotoUploadTask images);

        void showAddPhotoDialog();

        void replace(BucketPhotoUploadTask photoUploadTask, BucketPhoto bucketPhoto);

        void deleteImage(BucketPhoto photo);

        void deleteImage(BucketPhotoUploadTask photo);
    }