package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedEntityDeletedEvent {

    FeedItem eventModel;

    public FeedEntityDeletedEvent(FeedItem eventModel) {
        this.eventModel = eventModel;
    }

    public FeedItem getEventModel() {
        return eventModel;
    }
}
