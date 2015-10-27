package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.BaseFeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PostFragment;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

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

    private void calculateScrollArea(){
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        int profilePhotoHeight = getResources().getDimensionPixelSize(R.dimen.profile_cover_height);
        scrollArea = profilePhotoHeight - actionBarHeight;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

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
        fragmentCompass.setContainerId(R.id.container_details_floating);
        BaseFragment baseFragment = fragmentCompass.getCurrentFragment();
        if (baseFragment instanceof PostFragment) {
            showPostContainer();
        }
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
        NavigationBuilder.create()
                .with(activityRouter)
                .move(Route.FRIENDS);
    }

    @Override
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
    protected BaseArrayListAdapter getAdapter() {
        return new IgnoreFirstItemAdapter(feedView.getContext(), this);
    }

    @Override
    public void notifyUserChanged() {
        feedView.getAdapter().notifyDataSetChanged();
    }

}
