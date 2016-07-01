package com.worldventures.dreamtrips.modules.feed.view.util;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.StateRecyclerView;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;

import javax.inject.Inject;

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
