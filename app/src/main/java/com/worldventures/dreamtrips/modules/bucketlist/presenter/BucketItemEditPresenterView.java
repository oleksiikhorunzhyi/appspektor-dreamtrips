package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;

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

    IBucketPhotoView getBucketPhotosView();

}