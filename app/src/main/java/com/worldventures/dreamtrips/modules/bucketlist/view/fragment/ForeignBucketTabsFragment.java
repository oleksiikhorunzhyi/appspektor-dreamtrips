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

    public static String EXTRA_USER_ID = "EXTRA_USER_ID";

    @Override
    protected ForeignBucketTabPresenter createPresenter(Bundle savedInstanceState) {
        return new ForeignBucketTabPresenter(getArguments().getInt(EXTRA_USER_ID));
    }

    @NonNull
    @Override
    protected Bundle createListFragmentArgs(int position) {
        Bundle args = super.createListFragmentArgs(position);
        args.putBoolean(BucketListFragment.BUNDLE_DRAG_ENABLED, false);
        return args;
    }

    @Override
    protected Route getDetailsRoute() {
        return Route.DETAIL_FOREIGN_BUCKET;
    }
}
