package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

@Layout(R.layout.adapter_item_feed_bucket_event)
public class FeedBucketEventCell extends FeedHeaderCell<FeedBucketEventModel> {

    public FeedBucketEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
    }

    @Override
    public void prepareForReuse() {

    }
}
