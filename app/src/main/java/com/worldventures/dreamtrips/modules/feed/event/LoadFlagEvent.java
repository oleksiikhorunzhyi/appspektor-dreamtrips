package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

public class LoadFlagEvent {
    private FeedHeaderCell feedHeaderCell;

    public LoadFlagEvent(FeedHeaderCell feedHeaderCell) {
        this.feedHeaderCell = feedHeaderCell;
    }

    public FeedHeaderCell getFeedCell() {
        return feedHeaderCell;
    }
}
