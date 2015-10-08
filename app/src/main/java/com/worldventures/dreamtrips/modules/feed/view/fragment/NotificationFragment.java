package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.mobile.util.WeakHandler;
import com.eowise.recyclerview.stickyheaders.OnHeaderClickListener;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeImageView;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.NotificationHeaderAdapter;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;


@Layout(R.layout.fragment_notification)
@MenuResource(R.menu.menu_notifications)
public class NotificationFragment extends BaseFragment<NotificationPresenter> implements NotificationPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.notifications)
    EmptyRecyclerView notifications;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    NotificationAdapter adapter;
    RecyclerViewStateDelegate stateDelegate;

    BadgeImageView friendsBadge;

    WeakHandler weakHandler = new WeakHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        friendsBadge = (BadgeImageView) menu.findItem(R.id.action_friend_requests).getActionView();
        friendsBadge.setOnClickListener(v ->
                NavigationBuilder.create()
                        .with(activityRouter)
                        .data(new FriendMainBundle(FriendMainBundle.REQUESTS))
                        .attach(Route.FRIENDS));
        getPresenter().refreshRequestsCount();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new NotificationAdapter(getActivity(), injectorProvider);
        adapter.setHasStableIds(true);

        this.adapter.registerCell(PhotoFeedItem.class, NotificationCell.class);
        this.adapter.registerCell(TripFeedItem.class, NotificationCell.class);
        this.adapter.registerCell(BucketFeedItem.class, NotificationCell.class);
        this.adapter.registerCell(PostFeedItem.class, NotificationCell.class);
        this.adapter.registerCell(UndefinedFeedItem.class, NotificationCell.class);
        this.adapter.registerCell(LoadMoreModel.class, LoaderCell.class);

        notifications.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        notifications.setLayoutManager(layoutManager);

        notifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = notifications.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });
        stateDelegate.setRecyclerView(notifications);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);

        NotificationHeaderAdapter headerAdapter = new NotificationHeaderAdapter(adapter.getItems(), R.layout.adapter_item_notification_divider, item -> () -> {
            if (item instanceof FeedItem) {
                return getString(((FeedItem) item).getReadAt() == null ? R.string.notifaction_new : R.string.notifaction_older);
            } else return null;
        });
        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setStickyHeadersAdapter(headerAdapter, false)
                .setOnHeaderClickListener((header, headerId) -> {
                    // make sticky header clickable to make items below it not clickable
                })
                .setRecyclerView(notifications)
                .build();
        notifications.addItemDecoration(decoration);
        notifications.setEmptyView(emptyView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    protected void restoreState(Bundle savedInstanceState) {
        super.restoreState(savedInstanceState);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stateDelegate.onDestroyView();
    }

    @Override
    protected NotificationPresenter createPresenter(Bundle savedInstanceState) {
        return new NotificationPresenter();
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

    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public void setRequestsCount(int count) {
        if (friendsBadge != null) {
            friendsBadge.setBadgeValue(count);
        }
    }

    public static class NotificationAdapter extends BaseArrayListAdapter {

        private static final long LOADER_ID = Long.MIN_VALUE;

        public NotificationAdapter(Context context, Provider<Injector> injector) {
            super(context, injector);
        }

        @Override
        public long getItemId(int position) {
            long id = super.getItemId(position);
            return id != RecyclerView.NO_ID ? id : LOADER_ID;
        }

    }
}
