package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;

public class FeedItemAddedEvent {
    BaseEventModel baseEventModel;

    public FeedItemAddedEvent(BaseEventModel baseEventModel) {
        this.baseEventModel = baseEventModel;
    }

    public BaseEventModel getBaseEventModel() {
        return baseEventModel;
    }
}
