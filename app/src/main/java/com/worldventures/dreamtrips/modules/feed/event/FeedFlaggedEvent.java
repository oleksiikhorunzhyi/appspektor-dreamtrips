package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

public class FeedFlaggedEvent {

    private IFeedObject entity;

    private String nameOfReason;

    public FeedFlaggedEvent(IFeedObject entity, String nameOfReason) {
        this.entity = entity;
        this.nameOfReason = nameOfReason;
    }

    public IFeedObject getEntity() {
        return entity;
    }

    public String getNameOfReason() {
        return nameOfReason;
    }
}
