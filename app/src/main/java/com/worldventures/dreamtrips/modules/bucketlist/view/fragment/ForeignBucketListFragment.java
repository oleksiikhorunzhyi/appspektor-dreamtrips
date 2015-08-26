package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketListPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_bucket_list)
@MenuResource(R.menu.menu_bucket)
public class ForeignBucketListFragment extends BucketListFragment<ForeignBucketListPresenter> {

    @InjectView(R.id.ll_empty_view_container)
    View emptyViewContainer;

    protected boolean isDragEnabled() {
        return false;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        emptyViewContainer.setVisibility(View.GONE);
    }

    @Override
    protected ForeignBucketListPresenter createPresenter(Bundle savedInstanceState) {
        BucketTabsPresenter.BucketType type = (BucketTabsPresenter.BucketType) getArguments().getSerializable(BUNDLE_TYPE);
        return new ForeignBucketListPresenter(type, getObjectGraph());
    }
}
