package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeImageView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_feed)
@MenuResource(R.menu.menu_activity_feed)
public class FeedFragment extends BaseFeedFragment<FeedPresenter, FeedBundle>
        implements FeedPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.fab_post)
    FloatingActionButton fabPost;

    BadgeImageView friendsBadge;

    @Optional
    @InjectView(R.id.detail_container)
    protected View detailsContainer;

    WeakHandler handler = new WeakHandler();

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        restorePostIfNeeded();
    }

    private void restorePostIfNeeded() {
        fragmentCompass.setContainerId(R.id.container_details_floating);
        BaseFragment baseFragment = fragmentCompass.getCurrentFragment();
        if (baseFragment instanceof PostFragment) {
            showPostContainer();
        }
    }

    @OnClick(R.id.fab_post)
    void onPostClicked() {
        openPost();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_friend_requests);
        friendsBadge = (BadgeImageView) MenuItemCompat.getActionView(item);
        friendsBadge.setOnClickListener(v ->
                NavigationBuilder.create()
                        .with(activityRouter)
                        .data(new FriendMainBundle(FriendMainBundle.REQUESTS))
                        .attach(Route.FRIENDS));
        getPresenter().refreshRequestsCount();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                actionFilter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionFilter() {
        FeedPresenter presenter = getPresenter();
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        CirclesFilterPopupWindow filterPopupWindow = new CirclesFilterPopupWindow(getContext());
        filterPopupWindow.setCircles(presenter.getFilterCircles());
        filterPopupWindow.setAnchorView(menuItemView);
        filterPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            filterPopupWindow.dismiss();
            presenter.applyFilter((Circle) parent.getItemAtPosition(position));
        });
        filterPopupWindow.show();
        filterPopupWindow.setCheckedCircle(presenter.getAppliedFilterCircle());
    }

    @Override
    protected FeedPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedPresenter();
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return new BaseArrayListAdapter<>(feedView.getContext(), injectorProvider);
    }

    public void openPost() {
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);
        //
        NavigationBuilder.create()
                .with(fragmentCompass)
                .attach(Route.POST_CREATE);
    }

    @Override
    public void setRequestsCount(int count) {
        if (friendsBadge != null) {
            friendsBadge.setBadgeValue(count);
        }
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
