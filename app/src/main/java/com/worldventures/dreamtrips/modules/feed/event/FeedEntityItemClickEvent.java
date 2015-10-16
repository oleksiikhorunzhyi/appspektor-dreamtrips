package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedEntityItemClickEvent {
    private FeedItem feedItem;

    public FeedEntityItemClickEvent(FeedItem feedItem) {

        this.feedItem = feedItem;
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }
}
