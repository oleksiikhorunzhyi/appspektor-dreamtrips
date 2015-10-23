package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedItemDetailsCell;

@Layout(R.layout.fragment_comments_with_details)
public class FeedItemDetailsFragment extends CommendableFragment<FeedItemDetailsPresenter, FeedItemDetailsBundle> implements FeedItemDetailsPresenter.View {

    private FragmentCompass childCompass;

    @Override
    protected FeedItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedItemDetailsPresenter(getArgs().getFeedItem());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        childCompass = new FragmentCompass((BaseActivity) getActivity(), R.id.comments_additional_info_container);
        childCompass.setSupportFragmentManager(getChildFragmentManager());
        childCompass.disableBackStack();
        //
        adapter.registerCell(PostFeedItem.class, FeedItemDetailsCell.class);
        adapter.registerCell(BucketFeedItem.class, FeedItemDetailsCell.class);
    }

    @Override
    public void setFeedItem(FeedItem feedItem) {
        adapter.addItem(0, feedItem);
        adapter.notifyItemInserted(0);
    }

    @Override
    public void updateFeedItem(FeedItem feedItem) {
        adapter.updateItem(feedItem);
        if (isTabletLandscape() && childCompass.empty()) {
            NavigationBuilder.create()
                    .with(childCompass)
                    .data(new FeedAdditionalInfoBundle(feedItem.getItem().getOwner()))
                    .move(Route.FEED_ITEM_ADDITIONAL_INFO);
        }
    }

    @Override
    protected int getAdditionalItemsCount() {
        return super.getAdditionalItemsCount() + 1;
    }

    @Override
    protected int getLoadMorePosition() {
       return super.getLoadMorePosition() + 1;
    }
}
