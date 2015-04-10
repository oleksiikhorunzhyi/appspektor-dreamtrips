package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

    public BucketItemDetailsPresenter(View view, Bundle bundle) {
        super(view, bundle);
    }

    public void onEdit() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketActivity.EXTRA_TYPE, type);
        bundle.putSerializable(BucketActivity.EXTRA_ITEM, bucketItem);
        if (view.isTabletLandscape()) {
            view.showEditContainer();
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_edit);
            fragmentCompass.add(Route.BUCKET_EDIT, bundle);
        } else {
            activityRouter.openBucketItemEditActivity(bundle);
        }
    }

    public void onStatusUpdated(boolean status) {
        if (status != bucketItem.isDone()) {
            view.disableCheckbox();
            BucketBasePostItem bucketBasePostItem = new BucketBasePostItem();
            bucketBasePostItem.setStatus(status);
            saveBucketItem(bucketBasePostItem);
        }
    }

    public void onEvent(MarkBucketItemDoneEvent event) {
        if (event.getBucketItem().equals(bucketItem)) {
            bucketItem = event.getBucketItem();
            syncUI();
        }
    }

    @Override
    protected void syncUI() {
        super.syncUI();
        view.setCategory(bucketItem.getCategoryName());
        view.setCover(bucketItem.getCoverUrl());
        view.updatePhotos();
    }

    @Override
    protected void onSuccess(BucketItem bucketItemUpdated) {
        super.onSuccess(bucketItemUpdated);
        view.enableCheckbox();
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(String category);

        void setCover(String imageUrl);

        void showEditContainer();

        void updatePhotos();

        void disableCheckbox();

        void enableCheckbox();
    }

}
