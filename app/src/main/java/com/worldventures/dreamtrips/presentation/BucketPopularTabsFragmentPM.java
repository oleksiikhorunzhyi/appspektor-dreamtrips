package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

public class BucketPopularTabsFragmentPM extends BasePresentation<BasePresentation.View> {


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
