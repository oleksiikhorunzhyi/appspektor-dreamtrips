package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

public class FeedEntityChangedEvent {

    IFeedObject entity;

    public FeedEntityChangedEvent(IFeedObject entity) {
        this.entity = entity;
    }

    public IFeedObject getFeedEntity() {
        return entity;
    }
}
