package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_bucket_popular)
public class BucketListPopuralFragment extends BaseFragment<BucketPopularPresenter> implements BucketPopularPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.recyclerViewBuckets)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    protected View emptyView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private LoaderRecycleAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        setManager();

        this.recyclerView.setEmptyView(emptyView);
        this.arrayListAdapter = new LoaderRecycleAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(PopularBucketItem.class, BucketPopularCell.class);
        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setManager();
    }

    private void setManager() {
        RecyclerView.LayoutManager layoutManager;
        if (isTabletLandscape()) {
            layoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            layoutManager = new LinearLayoutManager(getActivity());
        }

        this.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return arrayListAdapter;
    }

    @Override
    public void finishLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(false));
    }

    @Override
    public void startLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    protected BucketPopularPresenter createPresenter(Bundle savedInstanceState) {
        BucketTabsPresenter.BucketType type = (BucketTabsPresenter.BucketType) getArguments().getSerializable(BucketActivity.EXTRA_TYPE);
        return new BucketPopularPresenter(this, type);
    }
}