package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.CommentIconClickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ProfileClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseFeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class BaseFeedFragment<P extends BaseFeedPresenter, T extends Parcelable>
        extends RxBaseFragmentWithArgs<P, T>
        implements BaseFeedPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.feed)
    protected FeedView feedView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout swipeContainer;
    @Optional
    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;
    @Optional
    @InjectView(R.id.tv_search_friends)
    protected TextView tvSearchFriends;
    @Optional
    @InjectView(R.id.arrow)
    protected ImageView ivArrow;
    protected BaseArrayListAdapter adapter;
    @Optional
    @InjectView(R.id.detail_container)
    protected View detailsContainer;

    WeakHandler weakHandler;
    Bundle savedInstanceState;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

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

        feedView.setEmptyView(emptyView);

        adapter = createAdapter();
        feedView.setup(savedInstanceState, adapter);

        feedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = feedView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = feedView.getLayoutManager().findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });

        if (tvSearchFriends != null)
            tvSearchFriends.setPaintFlags(tvSearchFriends.getPaintFlags()
                    | Paint.UNDERLINE_TEXT_FLAG);

        if (ivArrow != null && isPhoneLandscape())
            ivArrow.setVisibility(View.GONE);
    }

    @Optional
    @OnClick(R.id.tv_search_friends)
    public void onFriendsSearchClicked() {
        router.moveTo(Route.FRIEND_SEARCH, NavigationConfigBuilder.forActivity()
                .data(new FriendGlobalSearchBundle(""))
                .build());
    }

    protected abstract BaseArrayListAdapter createAdapter();

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

    public void onEvent(CommentIconClickedEvent event) {
        if (isVisibleOnScreen()) {
            Route detailsRoute = Route.FEED_ITEM_DETAILS;
            FeedDetailsBundle bundle = new FeedDetailsBundle(event.getFeedItem());
            if (tabletAnalytic.isTabletLandscape()) {
                bundle.setSlave(true);
            }
            bundle.setOpenKeyboard(true);
            router.moveTo(detailsRoute, NavigationConfigBuilder.forActivity()
                    .data(bundle)
                    .build());
        }
    }

    public void onEvent(ProfileClickedEvent event) {
        User user = event.getUser();
        openUser(user);
    }

    protected void openUser(User user) {
        router.moveTo(routeCreator.createRoute(user.getId()), NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(user))
                .build());
    }

    @Override
    public void refreshFeedItems(List<FeedItem> events, boolean needLoader) {
        adapter.clear();
        adapter.addItems(events);
        if (needLoader) adapter.addItem(new LoadMoreModel());
        adapter.notifyDataSetChanged();
    }

    private boolean isPhoneLandscape() {
        return !ViewUtils.isTablet(getActivity()) && ViewUtils.isLandscapeOrientation(getActivity());
    }

    @Override
    public void showEdit(BucketBundle bucketBundle) {
        int containerId = R.id.container_details_floating;
        bucketBundle.setLock(true);
        if (isTabletLandscape()) {
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forFragment()
                    .backStackEnabled(true)
                    .containerId(containerId)
                    .fragmentManager(getChildFragmentManager())
                    .data(bucketBundle)
                    .build());
            showContainer(containerId);
        } else {
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forActivity()
                    .data(bucketBundle)
                    .build());
        }
    }

    private void showContainer(int containerId) {
        View container = ButterKnife.findById(getActivity(), containerId);
        if (container != null) container.setVisibility(View.VISIBLE);
    }
}
