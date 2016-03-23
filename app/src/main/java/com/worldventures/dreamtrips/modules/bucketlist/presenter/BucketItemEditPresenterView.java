package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

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

    void addImages(List<UploadTask> tasks);

    void addImage(UploadTask uploadTask);

    void deleteImage(UploadTask task);

    void deleteImage(BucketPhoto bucketPhoto);

    void itemChanged(UploadTask uploadTask);

    void replace(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto);


}