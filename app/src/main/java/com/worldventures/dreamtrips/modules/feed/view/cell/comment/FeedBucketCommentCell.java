package com.worldventures.dreamtrips.modules.feed.view.cell.comment;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;

@Layout(R.layout.adapter_item_feed_bucket_comment)
public class FeedBucketCommentCell extends FeedBucketEventCell {

    public FeedBucketCommentCell(View view) {
        super(view);
    }

    @Override
    protected void openComments(BaseEventModel baseFeedModel) {
        //do nothing
    }
}
