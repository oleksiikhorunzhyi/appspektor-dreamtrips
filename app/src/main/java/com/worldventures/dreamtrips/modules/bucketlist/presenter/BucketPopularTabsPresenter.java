package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class BucketPopularTabsPresenter extends Presenter<Presenter.View> {


    public BucketPopularTabsPresenter(View view) {
        super(view);
    }

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsFragment.Type type = BucketTabsFragment.Type.values()[position];
        args.putSerializable(BucketActivity.EXTRA_TYPE, type);
        return args;
    }

}
