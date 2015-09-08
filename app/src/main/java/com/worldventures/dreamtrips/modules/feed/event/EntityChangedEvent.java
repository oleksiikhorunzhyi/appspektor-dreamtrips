package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

public class EntityChangedEvent {
    private IFeedObject entity;

    public EntityChangedEvent(IFeedObject entity) {
        this.entity = entity;
    }

    public IFeedObject getEntity() {
        return entity;
    }
}
