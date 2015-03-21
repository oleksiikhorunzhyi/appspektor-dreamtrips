package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.core.utils.events.QuickAddItemEvent;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketTabsFragmentPM extends BasePresenter<BucketTabsFragmentPM.View> {

    @Global
    @Inject
    EventBus eventBus;

    public BucketTabsFragmentPM(View view) {
        super(view);
    }

    public void addOwn(int position) {
        eventBus.post(new QuickAddItemEvent(BucketTabsFragment.Type.values()[position]));
    }

    public void addPopular(int position) {
        activityRouter.openBucketListEditActivity(BucketTabsFragment.Type.values()[position], Route.POPULAR_TAB_BUCKER);
    }

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsFragment.Type type = BucketTabsFragment.Type.values()[position];
        args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
        return args;
    }

    public interface View extends BasePresenter.View {
        boolean isTabletLandscape();
    }

}
