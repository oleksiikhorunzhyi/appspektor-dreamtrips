package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;

public class FeedObjectChangedEvent {

    BaseEventModel feedModel;

    public FeedObjectChangedEvent(BaseEventModel feedModel) {
        this.feedModel = feedModel;
    }

    public BaseEventModel getFeedObject() {
        return feedModel;
    }
}
