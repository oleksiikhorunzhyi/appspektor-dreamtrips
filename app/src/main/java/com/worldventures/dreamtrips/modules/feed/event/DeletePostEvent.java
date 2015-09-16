package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;

public class DeletePostEvent {

    public BaseEventModel feedItem;

    public DeletePostEvent(BaseEventModel feedItem) {
        this.feedItem = feedItem;
    }

    public BaseEventModel getEntity() {
        return feedItem;
    }
}
