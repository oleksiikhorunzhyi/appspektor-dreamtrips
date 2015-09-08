package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseFeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.DiffArrayListAdapter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;

import java.util.List;

import butterknife.InjectView;

public abstract class BaseFeedFragment<P extends BaseFeedPresenter, T extends Parcelable>
        extends BaseFragmentWithArgs<P, T>
        implements BaseFeedPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.feed)
    protected FeedView feedView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout swipeContainer;

    private WeakHandler weakHandler;
    private Bundle savedInstanceState;

    protected DiffArrayListAdapter adapter;

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

    protected abstract DiffArrayListAdapter getAdapter();

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
    public void refreshFeedItems(List<BaseEventModel> events) {
        adapter.itemsUpdated(events);
    }
}