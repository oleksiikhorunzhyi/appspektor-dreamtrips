package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeImageView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_feed)
@MenuResource(R.menu.menu_activity_feed)
public class FeedFragment extends BaseFeedFragment<FeedPresenter, FeedBundle>
        implements FeedPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    BadgeImageView friendsBadge;

    private CirclesFilterPopupWindow filterPopupWindow;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        restorePostIfNeeded();

        if (isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.additional_info_container);

            NavigationBuilder.create()
                    .with(fragmentCompass)
                    .data(new FeedAdditionalInfoBundle(getPresenter().getAccount()))
                    .attach(Route.FEED_LIST_ADDITIONAL_INFO);
        }

    }

    private void restorePostIfNeeded() {
        fragmentCompass.setContainerId(R.id.container_details_floating);
        BaseFragment baseFragment = fragmentCompass.getCurrentFragment();
        if (baseFragment instanceof PostFragment) {
            showPostContainer();
        }
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
        setRequestsCount(getPresenter().getFriendsRequestsCount());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (filterPopupWindow == null || filterPopupWindow.dismissPassed()) {
                    actionFilter();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        filterPopupWindow = null;
    }

    private void actionFilter() {
        FeedPresenter presenter = getPresenter();
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        filterPopupWindow = new CirclesFilterPopupWindow(getContext());
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
        return new BaseArrayListAdapter<>(feedView.getContext(), this);
    }

    private void openPost() {
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);
        //
        NavigationBuilder.create()
                .with(fragmentCompass)
                .attach(Route.POST_CREATE);
    }

    private void openSharePhoto() {
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);

        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(new PostBundle(null, PostBundle.PHOTO))
                .attach(Route.POST_CREATE);

    }

    @Override
    public void setRequestsCount(int count) {
        if (friendsBadge != null) {
            friendsBadge.setBadgeValue(count);
        }
    }

    @Optional
    @OnClick(R.id.share_post)
    protected void onPostClicked() {
        openPost();
    }

    @Optional
    @OnClick(R.id.share_photo)
    protected void onSharePhotoClick() {
        openSharePhoto();
    }

}
