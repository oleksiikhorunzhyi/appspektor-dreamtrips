package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

    public BucketItemDetailsPresenter(View view, Bundle bundle) {
        super(view, bundle);
    }

    @Override
    public void resume() {
        super.resume();
        view.setCategory(bucketItem.getCategory().getName());
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(String category);
    }

}
