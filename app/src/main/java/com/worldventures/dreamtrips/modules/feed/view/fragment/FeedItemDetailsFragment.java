package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;

@Layout(R.layout.fragment_comments_with_details)
public class FeedItemDetailsFragment extends CommendableFragment<FeedItemDetailsPresenter, FeedItemDetailsBundle> implements FeedItemDetailsPresenter.View {

    private static final int FEED_DETAILS_COUNT = 1;


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
    }

    @Override
    protected FeedItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedItemDetailsPresenter(getArgs().getFeedItem());
    }

    @Override
    public void updateFeedItem(FeedItem feedItem) {
        adapter.replaceItem(0, feedItem);
        adapter.notifyDataSetChanged();
        if (isTabletLandscape()) {
            fragmentCompass.removePost();
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.additional_info_container);

            NavigationBuilder.create()
                    .with(fragmentCompass)
                    .data(new FeedAdditionalInfoBundle(feedItem.getItem().getUser()))
                    .attach(Route.FEED_ITEM_ADDITIONAL_INFO);
        }
    }

    @Override
    public void setFeedItem(FeedItem feedItem) {
        adapter.addItem(0, feedItem);
        adapter.notifyDataSetChanged();
    }

    protected int getHeaderCount() {
        return super.getHeaderCount() + FEED_DETAILS_COUNT;
    }

    @Override
    protected int getLoadMorePosition() {
        return 1;
    }
}
