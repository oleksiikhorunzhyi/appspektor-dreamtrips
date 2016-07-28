package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.CommentIconClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.view.custom.SideMarginsItemDecorator;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.model.ReloadFeedModel;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;
import com.worldventures.dreamtrips.modules.profile.view.cell.ProfileCell;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class ProfileFragment<T extends ProfilePresenter> extends RxBaseFragmentWithArgs<T, UserBundle>
        implements ProfilePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.profile_toolbar) Toolbar profileToolbar;
    @InjectView(R.id.profile_toolbar_title) TextView profileToolbarTitle;
    @InjectView(R.id.profile_user_status) TextView profileToolbarUserStatus;

    @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;

    private int scrollArea;

    private Bundle savedInstanceState;

    protected StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        calculateScrollArea();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileToolbar != null) {
            float percent = calculateOffset();
            setToolbarAlpha(percent);
        }
    }

    @Override
    public void onDestroyView() {
        setToolbarAlpha(100);
        super.onDestroyView();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        BaseDelegateAdapter adapter = createAdapter();
        statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
        statePaginatedRecyclerViewManager.init(adapter, savedInstanceState);
        statePaginatedRecyclerViewManager.setOnRefreshListener(this);
        statePaginatedRecyclerViewManager.setPaginationListener(() -> {
            if (!statePaginatedRecyclerViewManager.isNoMoreElements()) {
                fragmentWithFeedDelegate.addItem(new LoadMoreModel());
                fragmentWithFeedDelegate.notifyDataSetChanged();
            }
            getPresenter().onLoadNext();
        });
        statePaginatedRecyclerViewManager.addItemDecoration(new SideMarginsItemDecorator(true));
        statePaginatedRecyclerViewManager.setOffsetYListener(yOffset -> {
            float percent = calculateOffset();
            setToolbarAlpha(percent);
            if (percent >= 1.0) {
                profileToolbarTitle.setVisibility(View.VISIBLE);
                profileToolbarUserStatus.setVisibility(View.VISIBLE);
            } else {
                profileToolbarTitle.setVisibility(View.INVISIBLE);
                profileToolbarUserStatus.setVisibility(View.INVISIBLE);
            }
        });
        //
        fragmentWithFeedDelegate.init(adapter);
        registerAdditionalCells();
        registerCellDelegates();
        //
        initialToolbar();
    }

    @Override
    public void setUser(User user) {
        if (fragmentWithFeedDelegate.getItems().contains(user)) {
            fragmentWithFeedDelegate.updateItem(user);
        } else {
            fragmentWithFeedDelegate.addItem(0, user);
            fragmentWithFeedDelegate.notifyItemInserted(0);
        }
        //
        ProfileViewUtils.setUserStatus(user, profileToolbarUserStatus, getResources());
        profileToolbarTitle.setText(user.getFullName());
    }

    @Override
    public void updateItem(int position) {
        fragmentWithFeedDelegate.notifyItemChanged(position);
    }

    @Override
    public void openFriends() {
        fragmentWithFeedDelegate.openFriends(null);
    }

    @Override
    public void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle) {
        fragmentWithFeedDelegate.openBucketList(route, foreignBucketBundle);
    }

    @Override
    public void openTripImages(Route route, TripsImagesBundle tripImagesBundle) {
        fragmentWithFeedDelegate.openTripImages(route, tripImagesBundle);
    }

    @Override
    public void openPost() {
        fragmentWithFeedDelegate.openPost(getActivity().getSupportFragmentManager());
    }

    @Override
    public void notifyUserChanged() {
        fragmentWithFeedDelegate.notifyDataSetChanged();
    }

    @Override
    public void refreshFeedItems(List<FeedItem> events) {
        fragmentWithFeedDelegate.clearItems();
        fragmentWithFeedDelegate.addItems(events);
        fragmentWithFeedDelegate.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void startLoading() {
        statePaginatedRecyclerViewManager.startLoading();
    }

    @Override
    public void finishLoading() {
        statePaginatedRecyclerViewManager.finishLoading();
    }

    @Override
    public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
        statePaginatedRecyclerViewManager.updateLoadingStatus(loading, noMoreElements);
    }

    @Override
    public void showEdit(BucketBundle bucketBundle) {
        fragmentWithFeedDelegate.openBucketEdit(getActivity().getSupportFragmentManager(), isTabletLandscape(), bucketBundle);
    }

    public void onEvent(CommentIconClickedEvent event) {
        fragmentWithFeedDelegate.openComments(event.getFeedItem(), isVisibleOnScreen(), isTabletLandscape());
    }

    @Optional
    @OnClick(R.id.tv_search_friends)
    public void onFriendsSearchClicked() {
        fragmentWithFeedDelegate.openFriendsSearch();
    }

    protected abstract void initialToolbar();

    protected abstract BaseDelegateAdapter createAdapter();

    private float calculateOffset() {
        return Math.min(statePaginatedRecyclerViewManager.stateRecyclerView.getScrollOffset() / (float) scrollArea, 1);
    }

    private void setToolbarAlpha(float percentage) {
        Drawable c = profileToolbar.getBackground();
        int round = Math.round(Math.min(1, percentage * 2) * 255);
        c.setAlpha(round);
        profileToolbar.setBackgroundDrawable(c);
    }

    private void calculateScrollArea() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        int profilePhotoHeight = getResources().getDimensionPixelSize(R.dimen.profile_cover_height);
        scrollArea = profilePhotoHeight - actionBarHeight;
    }

    private void registerAdditionalCells() {
        fragmentWithFeedDelegate.registerAdditionalCell(User.class, ProfileCell.class);
    }

    private void registerCellDelegates() {
        fragmentWithFeedDelegate.registerDelegate(ReloadFeedModel.class, model -> getPresenter().onRefresh());
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
    }
}
