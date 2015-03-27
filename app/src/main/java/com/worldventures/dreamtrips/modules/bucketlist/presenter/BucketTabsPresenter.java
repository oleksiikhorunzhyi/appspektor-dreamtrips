package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketTabsPresenter extends Presenter<BucketTabsPresenter.View> {

    public BucketTabsPresenter(View view) {
        super(view);
    }

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsFragment.Type type = BucketTabsFragment.Type.values()[position];
        args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
        return args;
    }

    public interface View extends Presenter.View {
    }

}
