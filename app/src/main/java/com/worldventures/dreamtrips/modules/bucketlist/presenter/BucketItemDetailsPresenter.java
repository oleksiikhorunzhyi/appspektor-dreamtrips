package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

    public BucketItemDetailsPresenter(View view, Bundle bundle) {
        super(view, bundle);
    }

    public void onEdit() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketActivity.EXTRA_TYPE, type);
        bundle.putSerializable(BucketActivity.EXTRA_ITEM, bucketItem);
        activityRouter.openBucketItemEditActivity(bundle);
    }

    @Override
    public void resume() {
        super.resume();
        view.setCategory(bucketItem.getCategoryName());
        view.setCover(bucketItem.getCoverUrl());
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(String category);

        void setCover(String imageUrl);
    }

}
