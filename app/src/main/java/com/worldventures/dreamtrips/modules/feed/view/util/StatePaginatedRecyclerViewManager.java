package com.worldventures.dreamtrips.modules.feed.view.util;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.view.custom.StateRecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class StatePaginatedRecyclerViewManager {

    @InjectView(R.id.recyclerView)
    public StateRecyclerView stateRecyclerView;
    @InjectView(R.id.swipe_container)
    public SwipeRefreshLayout swipeContainer;

    private WeakHandler weakHandler;
    private PaginationViewManager paginationViewManager;

    public StatePaginatedRecyclerViewManager(View rootView) {
        ButterKnife.inject(this, rootView);
        weakHandler = new WeakHandler();
    }

    public void init(BaseArrayListAdapter adapter, Bundle savedInstanceState) {
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);
        //
        stateRecyclerView.setup(savedInstanceState, adapter);
        //
        paginationViewManager = new PaginationViewManager(stateRecyclerView);
    }

    public boolean isNoMoreElements() {
        return paginationViewManager.isNoMoreElements();
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        swipeContainer.setOnRefreshListener(onRefreshListener);
    }

    public void setPaginationListener(PaginationViewManager.PaginationListener paginationListener) {
        paginationViewManager.setPaginationListener(paginationListener);
    }

    public void setOffsetYListener(StateRecyclerView.OffsetYListener offsetYListener) {
        stateRecyclerView.setOffsetYListener(offsetYListener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        stateRecyclerView.addItemDecoration(itemDecoration);
    }

    public void startLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        });
    }

    public void finishLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(false);
        });
    }

    public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
        paginationViewManager.updateLoadingStatus(loading, noMoreElements);
    }
}
