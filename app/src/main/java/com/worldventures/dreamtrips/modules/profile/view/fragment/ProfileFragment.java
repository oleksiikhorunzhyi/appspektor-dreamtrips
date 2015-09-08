package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.view.adapter.DiffArrayListAdapter;
import com.worldventures.dreamtrips.modules.feed.view.fragment.BaseFeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PostFragment;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;


public abstract class ProfileFragment<T extends ProfilePresenter> extends BaseFeedFragment<T, UserBundle>
        implements ProfilePresenter.View {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.profile_toolbar)
    protected Toolbar profileToolbar;

    @InjectView(R.id.profile_toolbar_title)
    protected TextView profileToolbarTitle;

    @InjectView(R.id.profile_user_status)
    protected TextView profileToolbarUserStatus;

    private int screenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenHeight = ViewUtils.getScreenHeight(getActivity());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        feedView.setOffsetYListener(yOffset -> {
            float percent = calculateOffset();
            setToolbarAlpha(percent);
            if (percent >= 0.6) {
                profileToolbarTitle.setVisibility(View.VISIBLE);
                profileToolbarUserStatus.setVisibility(View.VISIBLE);
            } else {
                profileToolbarTitle.setVisibility(View.INVISIBLE);
                profileToolbarUserStatus.setVisibility(View.INVISIBLE);
            }
        });

        boolean isMainActivity = getActivity() instanceof MainActivity;
        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            profileToolbar.setNavigationIcon(isMainActivity ? R.drawable.ic_menu_hamburger : R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            profileToolbar.setNavigationOnClickListener(view -> {
                if (isMainActivity) {
                    ((MainActivity) getActivity()).openLeftDrawer();
                } else {
                    getActivity().onBackPressed();
                }
            });
        }
        restorePostIfNeeded();
    }

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
            adapter.itemUpdated(user);
        } else {
            adapter.addItem(0, user);
            adapter.notifyItemInserted(0);
        }

        ProfileViewUtils.setUserStatus(user, profileToolbarUserStatus, getResources());
        profileToolbarTitle.setText(user.getFullName());
    }

    private float calculateOffset() {
        return Math.min(feedView.getScrollOffset() / (float) screenHeight, 1);
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
    protected DiffArrayListAdapter getAdapter() {
        return new IgnoreFirstItemAdapter(feedView.getContext(), injectorProvider);
    }

    @Override
    public void notifyUserChanged() {
        feedView.getAdapter().notifyItemChanged(0);
    }

    private void showPostContainer() {
        View container = ButterKnife.findById(getActivity(), R.id.container_details_floating);
        if (container != null) container.setVisibility(View.VISIBLE);
    }

}
