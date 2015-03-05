package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;
import com.worldventures.dreamtrips.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;


import javax.inject.Inject;

public class BucketTabsFragmentPM extends BasePresentation {

    @Inject
    BucketListSelectionStorage bucketListSelectionStorage;

    public BucketTabsFragmentPM(View view) {
        super(view);
    }

    public void filterEnabled(boolean isEnabled) {
        bucketListSelectionStorage.getSelection().isFilterEnabled = isEnabled;
        bucketListSelectionStorage.save();
    }

    public boolean isFilterEnabled() {
        return bucketListSelectionStorage.getSelection().isFilterEnabled;
    }

    public void addOwn(int position) {
        activityRouter.openBucketListEditActivity(BucketTabsFragment.Type.values()[position], State.QUICK_INPUT);
    }

    public void addPopular(int position) {
        activityRouter.openBucketListEditActivity(BucketTabsFragment.Type.values()[position], State.POPULAR_BUCKET);
    }

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsFragment.Type type = BucketTabsFragment.Type.values()[position];
        args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
        return args;
    }
}
