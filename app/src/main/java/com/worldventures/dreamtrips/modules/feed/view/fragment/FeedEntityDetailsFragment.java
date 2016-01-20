package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedEntityDetailsCell;

@Layout(R.layout.fragment_comments_with_entity_details)
public class FeedEntityDetailsFragment extends FeedDetailsFragment<FeedDetailsPresenter> {

    @Override
    protected FeedDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedDetailsPresenter(getArgs().getFeedItem());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
    }

    @Override
    protected void registerCells() {
        adapter.registerCell(BucketFeedItem.class, FeedEntityDetailsCell.class);
        adapter.registerCell(TripFeedItem.class, FeedEntityDetailsCell.class);
    }
}
