package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
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
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;
import com.worldventures.dreamtrips.modules.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.NotificationHeaderAdapter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedUndefinedEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;


@Layout(R.layout.fragment_notification)
@MenuResource(R.menu.menu_notifications)
public class NotificationFragment extends BaseFragment<NotificationPresenter> implements NotificationPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.notifications)
    RecyclerView notifications;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private NotificationAdapter adapter;

    private RecyclerViewStateDelegate stateDelegate;

    WeakHandler weakHandler = new WeakHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_friend_requests:
                NavigationBuilder.create()
                        .with(activityRouter)
                        .attach(Route.FRIEND_REQUESTS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new NotificationAdapter(getActivity(), injectorProvider);
        adapter.setHasStableIds(true);

        this.adapter.registerCell(FeedPhotoEventModel.class, NotificationCell.class);
        this.adapter.registerCell(FeedTripEventModel.class, NotificationCell.class);
        this.adapter.registerCell(FeedBucketEventModel.class, NotificationCell.class);
        this.adapter.registerCell(FeedPostEventModel.class, NotificationCell.class);

        this.adapter.registerCell(FeedUndefinedEventModel.class, FeedUndefinedEventCell.class);

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


        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(notifications)
                .setStickyHeadersAdapter(new NotificationHeaderAdapter(adapter.getItems(),
                        R.layout.adapter_item_notification_divider), false)
                .build();

        notifications.addItemDecoration(decoration);

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
    public void refreshFeedItems(List<BaseEventModel> events) {
        adapter.addItems(events);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }


    public static class NotificationAdapter extends BaseArrayListAdapter<BaseEventModel> {

        public NotificationAdapter(Context context, Provider<Injector> injector) {
            super(context, injector);
        }

        @Override
        public long getItemId(int position) {
            return super.getItem(position).getItem().getUid().hashCode();
        }

    }
}
