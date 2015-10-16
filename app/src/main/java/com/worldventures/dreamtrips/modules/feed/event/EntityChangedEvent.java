package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class EntityChangedEvent {
    private FeedEntity entity;

    public EntityChangedEvent(FeedEntity entity) {
        this.entity = entity;
    }

    public FeedEntity getEntity() {
        return entity;
    }
}
