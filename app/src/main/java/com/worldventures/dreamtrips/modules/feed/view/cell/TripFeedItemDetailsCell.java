package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;


@Layout(R.layout.adapter_item_feed_details_wrapper)
public class TripFeedItemDetailsCell extends FeedItemDetailsCell {
    @InjectView(R.id.feed_item_header)
    ViewGroup feedItemHeader;

    public TripFeedItemDetailsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        feedItemHeader.setVisibility(View.GONE);
    }
}
