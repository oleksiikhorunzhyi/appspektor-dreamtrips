package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

public class FeedEntityCommentedEvent {

    IFeedObject entity;

    public FeedEntityCommentedEvent(IFeedObject entity) {
        this.entity = entity;
    }

    public IFeedObject getFeedEntity() {
        return entity;
    }
}
