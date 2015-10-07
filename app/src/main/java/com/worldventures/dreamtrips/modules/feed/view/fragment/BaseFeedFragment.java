package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseFeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public abstract class BaseFeedFragment<P extends BaseFeedPresenter, T extends Parcelable>
        extends BaseFragmentWithArgs<P, T>
        implements BaseFeedPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.feed)
    protected FeedView feedView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout swipeContainer;

    private WeakHandler weakHandler;
    private Bundle savedInstanceState;

    protected BaseArrayListAdapter adapter;

    @Optional
    @InjectView(R.id.detail_container)
    protected View detailsContainer;

    WeakHandler handler = new WeakHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);
        swipeContainer.setOnRefreshListener(this);

        adapter = getAdapter();
        feedView.setup(savedInstanceState, adapter);

        feedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = feedView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = feedView.getLayoutManager().findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });
    }

    protected void showPostContainer() {
        View container = ButterKnife.findById(getActivity(), R.id.container_details_floating);
        if (container != null) container.setVisibility(View.VISIBLE);
    }

    protected abstract BaseArrayListAdapter getAdapter();

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(false);
        });
    }

    @Override
    public void refreshFeedItems(List<FeedItem> events, boolean needLoader) {
        adapter.clearAndUpdateItems(events);
        if (needLoader) adapter.addItem(new LoadMoreModel());
    }

    @Override
    public void openDetails(FeedItem feedItem) {
        FeedEntityDetailsBundle bundle = new FeedEntityDetailsBundle(feedItem);
        Route detailsRoute = Route.FEED_ENTITY_DETAILS;
        if (isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
            fragmentCompass.setContainerId(R.id.detail_container);
            fragmentCompass.clear();
            bundle.setSlave(true);
            NavigationBuilder.create()
                    .with(fragmentCompass)
                    .data(bundle)
                    .attach(detailsRoute);
            showDetailsContainer();
        } else {
            hideDetailContainer();
            NavigationBuilder.create()
                    .with(activityRouter)
                    .data(bundle)
                    .move(detailsRoute);
        }
    }

    public void showDetailsContainer() {
        if (detailsContainer != null)
            handler.post(() -> detailsContainer.setVisibility(View.VISIBLE));
    }

    public void hideDetailContainer() {
        if (detailsContainer != null)
            handler.post(() -> detailsContainer.setVisibility(View.GONE));
    }
}
