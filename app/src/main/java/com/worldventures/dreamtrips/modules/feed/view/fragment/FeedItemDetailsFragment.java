package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.IFeedTabletViewDelegate;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;

@Layout(R.layout.fragment_comments_with_details)
public class FeedItemDetailsFragment extends CommentsFragment<FeedItemDetailsPresenter, FeedItemDetailsBundle> implements FeedItemDetailsPresenter.View {

    private static final int FEED_DETAILS_COUNT = 1;

    @Inject
    IFeedTabletViewDelegate feedTabletViewManager;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        feedTabletViewManager.setRootView(rootView);
    }

    @Override
    protected FeedItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedItemDetailsPresenter(getArgs().getFeedItem());
    }

    @Override
    public void updateFeedItem(FeedItem feedItem) {
        adapter.replaceItem(0, feedItem);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setFeedItem(FeedItem feedItem) {
        adapter.addItem(0, feedItem);
        adapter.notifyDataSetChanged();
        User user = getArgs().getFeedItem().getItem().getUser();
        feedTabletViewManager.setUser(user, false);
        feedTabletViewManager.setOnUserClick(() -> NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(user))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(user.getId())));
    }

    protected int getHeaderCount() {
        return super.getHeaderCount() + FEED_DETAILS_COUNT;
    }

    @Override
    protected int getLoadMorePosition() {
        return 1;
    }
}
