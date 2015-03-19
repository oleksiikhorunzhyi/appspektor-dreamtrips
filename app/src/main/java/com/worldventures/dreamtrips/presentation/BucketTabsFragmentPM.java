package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;
import com.worldventures.dreamtrips.utils.busevents.QuickAddItemEvent;
import com.worldventures.dreamtrips.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;


import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketTabsFragmentPM extends BasePresentation<BucketTabsFragmentPM.View> {

    @Global
    @Inject
    EventBus eventBus;

    public BucketTabsFragmentPM(View view) {
        super(view);
    }

    public void addOwn(int position) {
        eventBus.post(new QuickAddItemEvent(BucketTabsFragment.Type.values()[position]));
/*
        if (!view.isTabletLandscape()) {
            activityRouter.openBucketListEditActivity(BucketTabsFragment.Type.values()[position], State.QUICK_INPUT);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(BucketListEditActivity.EXTRA_TYPE, BucketTabsFragment.Type.values()[position]);
            fragmentCompass.setContainerId(R.id.container_child);
            fragmentCompass.add(State.QUICK_INPUT, bundle);
        }
*/
    }

    public void addPopular(int position) {
        activityRouter.openBucketListEditActivity(BucketTabsFragment.Type.values()[position], State.POPULAR_TAB_BUCKER);
    }

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsFragment.Type type = BucketTabsFragment.Type.values()[position];
        args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
        return args;
    }

    public interface View extends BasePresentation.View {
        boolean isTabletLandscape();
    }

}
