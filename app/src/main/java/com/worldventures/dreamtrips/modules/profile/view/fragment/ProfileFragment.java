package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentsFragment;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icicle;


public abstract class ProfileFragment<T extends ProfilePresenter> extends BaseFragment<T>
        implements ProfilePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.profile_toolbar)
    protected Toolbar profileToolbar;

    @InjectView(R.id.profile_toolbar_title)
    protected TextView profileToolbarTitle;

    @InjectView(R.id.profile_user_status)
    protected TextView profileToolbarUserStatus;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.feedview)
    FeedView feedView;

    private WeakHandler weakHandler;
    private Bundle savedInstanceState;

    private IgnoreFirstItemAdapter adapter;

    @Icicle
    ArrayList<Object> items;
    private int screenHeight;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (adapter != null) {
            List<Object> items = adapter.getItems();
            this.items = new ArrayList<>(items);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
        screenHeight = ViewUtils.getScreenHeight(getActivity());
    }


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);
        layoutConfiguration();
        adapter = new IgnoreFirstItemAdapter(feedView.getContext(), injectorProvider);
        feedView.setup(savedInstanceState, adapter);
        if (items != null) {
            adapter.addItems(items);
        }
        adapter.notifyDataSetChanged();

        feedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = feedView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = feedView.getLayoutManager().findLastVisibleItemPosition();

                if (itemCount > 0)
                    getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });

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

    private void layoutConfiguration() {
        swipeContainer.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void startLoading() {
        weakHandler.postDelayed(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        }, 100);
    }

    @Override
    public void finishLoading() {
        weakHandler.postDelayed(() -> {
            if (swipeContainer != null) {
                swipeContainer.setRefreshing(false);
            }
        }, 100);
    }

    @Override
    public void openComments(BaseEventModel baseFeedModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CommentsFragment.EXTRA_FEED_ITEM, baseFeedModel);
        //
        NavigationBuilder.create()
                .with(activityRouter)
                .args(bundle)
                .move(Route.PHOTO_COMMENTS);
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
    public BaseArrayListAdapter getAdapter() {
        return feedView.getAdapter();
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
