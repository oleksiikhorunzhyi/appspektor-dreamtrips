package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.view.custom.SideMarginsItemDecorator;
import com.worldventures.dreamtrips.modules.feed.view.fragment.BaseFeedFragment;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;

import butterknife.InjectView;

public abstract class ProfileFragment<T extends ProfilePresenter> extends BaseFeedFragment<T, UserBundle>
        implements ProfilePresenter.View {

    @InjectView(R.id.profile_toolbar)
    protected Toolbar profileToolbar;

    @InjectView(R.id.profile_toolbar_title)
    protected TextView profileToolbarTitle;

    @InjectView(R.id.profile_user_status)
    protected TextView profileToolbarUserStatus;

    private int scrollArea;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calculateScrollArea();
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

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        feedView.addItemDecoration(new SideMarginsItemDecorator());
        feedView.setOffsetYListener(yOffset -> {
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

        initialToolbar();
        restorePostIfNeeded();
    }

    protected abstract void initialToolbar();

    private void restorePostIfNeeded() {

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
    public void setUser(User user) {
        if (adapter.getItems().contains(user)) {
            adapter.updateItem(user);
        } else {
            adapter.addItem(0, user);
            adapter.notifyItemInserted(0);
        }

        ProfileViewUtils.setUserStatus(user, profileToolbarUserStatus, getResources());
        profileToolbarTitle.setText(user.getFullName());
    }

    private float calculateOffset() {
        return Math.min(feedView.getScrollOffset() / (float) scrollArea, 1);
    }

    private void setToolbarAlpha(float percentage) {
        Drawable c = profileToolbar.getBackground();
        int round = Math.round(Math.min(1, percentage * 2) * 255);
        c.setAlpha(round);
        profileToolbar.setBackgroundDrawable(c);
    }

    @Override
    public void openFriends() {
        router.moveTo(Route.FRIENDS, NavigationConfigBuilder.forActivity().build());
    }

    @Override
    public void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle) {
        router.moveTo(route, NavigationConfigBuilder.forActivity()
                .data(foreignBucketBundle)
                .build());
    }

    @Override
    public void openTripImages(Route route, TripsImagesBundle tripImagesBundle) {
        router.moveTo(route, NavigationConfigBuilder.forActivity()
                .data(tripImagesBundle)
                .build());
    }

    @Override
    public void openPost() {
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .containerId(R.id.container_details_floating)
                .build());
    }

    @Override
    protected BaseArrayListAdapter createAdapter() {
        return new IgnoreFirstItemAdapter(feedView.getContext(), this);
    }

    @Override
    public void notifyUserChanged() {
        feedView.getAdapter().notifyDataSetChanged();
    }

    protected void openUser(User user) {
        if (this.getPresenter().getUser().getId() != user.getId()) {
            super.openUser(user);
        } else {
            feedView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onDestroyView() {
        setToolbarAlpha(100);
        super.onDestroyView();
    }
}
