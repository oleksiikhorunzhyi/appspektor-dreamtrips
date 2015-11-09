package com.worldventures.dreamtrips.modules.feed.event;


import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;

public class FeedItemAnalyticEvent {

    String actionAttribute;
    String entityId;
    FeedEntityHolder.Type type;

    public FeedItemAnalyticEvent(String actionAttribute, String entityId, FeedEntityHolder.Type type) {
        this.actionAttribute = actionAttribute;
        this.entityId = entityId;
        this.type = type;
    }

    public String getActionAttribute() {
        return actionAttribute;
    }

    public String getEntityId() {
        return entityId;
    }

    public FeedEntityHolder.Type getType() {
        return type;
    }
}
