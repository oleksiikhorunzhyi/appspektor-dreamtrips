package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;

public class FeedEntityDeletedEvent {

    BaseEventModel eventModel;

    public FeedEntityDeletedEvent(BaseEventModel eventModel) {
        this.eventModel = eventModel;
    }

    public BaseEventModel getEventModel() {
        return eventModel;
    }
}
