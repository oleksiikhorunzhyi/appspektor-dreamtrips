package com.worldventures.dreamtrips.modules.feed.view.cell.comment;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;

@Layout(R.layout.adapter_item_feed_trip_comment)
public class FeedTripCommentCell extends FeedTripEventCell {

    public FeedTripCommentCell(View view) {
        super(view);
    }

    @Override
    protected void openComments(FeedItem baseFeedModel) {
        //do nothing
    }

}
