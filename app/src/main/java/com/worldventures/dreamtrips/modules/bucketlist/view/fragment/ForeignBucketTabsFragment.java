package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketTabPresenter;


@Layout(R.layout.fragment_bucket_tab)
@MenuResource(R.menu.menu_mock)
public class ForeignBucketTabsFragment extends BucketTabsFragment<ForeignBucketTabPresenter> {

    public String EXTRA_USER_ID = "EXTRA_USER_ID";

    @Override
    protected ForeignBucketTabPresenter createPresenter(Bundle savedInstanceState) {
        return new ForeignBucketTabPresenter(getArguments().getString(EXTRA_USER_ID));
    }


    @Override
    protected Route getDetailsRoute() {
        return Route.DETAIL_FOREIGN_BUCKET;
    }
}
