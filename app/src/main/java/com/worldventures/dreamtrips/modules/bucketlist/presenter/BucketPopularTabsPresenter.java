package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class BucketPopularTabsPresenter extends Presenter<Presenter.View> {

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsPresenter.BucketType type = BucketTabsPresenter.BucketType.values()[position];
        args.putSerializable(BucketListModule.EXTRA_TYPE, type);
        return args;
    }

}
