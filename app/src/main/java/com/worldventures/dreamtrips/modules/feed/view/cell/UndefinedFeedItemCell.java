package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemCell;

@Layout(R.layout.adapter_item_feed_undefined_event)
public class UndefinedFeedItemCell extends FeedItemCell<TripFeedItem> {

    public UndefinedFeedItemCell(View view) {
        super(view);
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
    }

    @Override
    protected void onDelete() {

    }

    @Override
    protected void onEdit() {

    }

    @Override
    protected void onMore() {

    }
}
