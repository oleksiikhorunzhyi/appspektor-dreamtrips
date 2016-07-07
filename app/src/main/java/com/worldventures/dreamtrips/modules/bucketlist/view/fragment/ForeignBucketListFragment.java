package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketListPresenter;

@Layout(R.layout.fragment_foreign_bucket_list)
public class ForeignBucketListFragment extends BucketListFragment<ForeignBucketListPresenter> {

    @Override
    protected boolean isDragEnabled() {
        return false;
    }

    @Override
    protected boolean isSwipeEnabled() {
        return false;
    }

    @Override
    protected ForeignBucketListPresenter createPresenter(Bundle savedInstanceState) {
        BucketItem.BucketType type = (BucketItem.BucketType) getArguments().getSerializable(BUNDLE_TYPE);
        return new ForeignBucketListPresenter(type);
    }
}