package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketTabPresenter;

@Layout(R.layout.fragment_bucket_tab)
@MenuResource(R.menu.menu_mock)
public class ForeignBucketTabsFragment extends BucketTabsFragment<ForeignBucketTabPresenter> {

    @Override
    protected ForeignBucketTabPresenter createPresenter(Bundle savedInstanceState) {
        return new ForeignBucketTabPresenter(getArgs().getUserId());
    }

    @NonNull
    @Override
    protected Class<ForeignBucketListFragment> getBucketListFragmentClass() {
        return ForeignBucketListFragment.class;
    }
}
