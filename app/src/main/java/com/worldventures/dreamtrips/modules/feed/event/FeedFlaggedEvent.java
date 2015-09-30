package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

public class FeedFlaggedEvent {

    private IFeedObject entity;

    public FeedFlaggedEvent(IFeedObject entity) {
        this.entity = entity;
    }

    public IFeedObject getEntity() {
        return entity;
    }
}
