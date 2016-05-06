package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoCreationItem;
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

    void hideMediaPicker();

    void showMediaPicker();

    void showLoading();

    void hideLoading();

    void addImage(BucketPhotoCreationItem uploadTask);

    void deleteImage(BucketPhoto bucketPhoto);

    void itemChanged(BucketPhotoCreationItem uploadTask);

    void replace(BucketPhotoCreationItem bucketPhotoUploadTask, BucketPhoto bucketPhoto);

    void deleteImage(BucketPhotoCreationItem task);

}