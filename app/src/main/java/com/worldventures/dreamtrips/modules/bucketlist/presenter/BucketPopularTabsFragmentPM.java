package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class BucketPopularTabsFragmentPM extends Presenter<Presenter.View> {


    public BucketPopularTabsFragmentPM(View view) {
        super(view);
    }

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsFragment.Type type = BucketTabsFragment.Type.values()[position];
        args.putSerializable(BucketListEditActivity.EXTRA_TYPE, type);
        return args;
    }

}
