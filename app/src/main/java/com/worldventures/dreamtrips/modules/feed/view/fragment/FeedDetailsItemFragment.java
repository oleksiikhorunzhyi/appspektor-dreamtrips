package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedDetailsItemPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedTabletViewManager;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

@Layout(R.layout.fragment_comments_with_details)
public class FeedDetailsItemFragment extends CommentsFragment<FeedDetailsItemPresenter, FeedEntityDetailsBundle> implements FeedDetailsItemPresenter.View {

    public static final int FEED_DETAILS_COUNT = 1;
    FeedTabletViewManager feedTabletViewManager;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        feedTabletViewManager = new FeedTabletViewManager(rootView);
    }

    @Override
    protected FeedDetailsItemPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedDetailsItemPresenter(getArgs().getFeedItem());
    }

    @Override
    public void updateHeader(FeedItem feedItem) {
        adapter.replaceItem(0, feedItem);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setHeader(FeedItem feedItem) {
        adapter.addItem(0, feedItem);
        adapter.notifyDataSetChanged();

        if (isTabletLandscape()) {
            User user = getArgs().getFeedItem().getItem().getUser();
            feedTabletViewManager.setUser(user, false);
            feedTabletViewManager.setOnUserClick(() -> NavigationBuilder.create().with(activityRouter)
                    .data(new UserBundle(user))
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .move(routeCreator.createRoute(user.getId())));
        }

    }

    protected int getHeaderCount() {
        return super.getHeaderCount() + FEED_DETAILS_COUNT;
    }

    @Override
    protected int getLoadMorePosition() {
        return 1;
    }
}
