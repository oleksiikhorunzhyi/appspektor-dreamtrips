package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedBundle;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.DiffArrayListAdapter;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_feed)
@MenuResource(R.menu.menu_notifications)
public class FeedFragment extends BaseFeedFragment<FeedPresenter, FeedBundle>
        implements FeedPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.fab_post)
    FloatingActionButton fabPost;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_friend_requests:
                NavigationBuilder.create()
                        .with(activityRouter)
                        .data(new FriendMainBundle(FriendMainBundle.REQUESTS))
                        .attach(Route.FRIENDS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected FeedPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedPresenter();
    }

    @Override
    public DiffArrayListAdapter<BaseEventModel> getAdapter() {
        return new DiffArrayListAdapter<>(feedView.getContext(), injectorProvider);
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

}
