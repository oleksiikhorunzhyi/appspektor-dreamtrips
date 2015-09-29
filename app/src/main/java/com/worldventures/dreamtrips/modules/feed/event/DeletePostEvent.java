package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class DeletePostEvent {

    public FeedItem feedItem;

    public DeletePostEvent(FeedItem feedItem) {
        this.feedItem = feedItem;
    }

    public FeedItem getEntity() {
        return feedItem;
    }
}
