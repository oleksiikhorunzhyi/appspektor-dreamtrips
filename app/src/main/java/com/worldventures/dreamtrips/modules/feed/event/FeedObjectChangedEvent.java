package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

public class FeedObjectChangedEvent {

    BaseFeedModel feedModel;

    public FeedObjectChangedEvent(BaseFeedModel feedModel) {
        this.feedModel = feedModel;
    }

    public BaseFeedModel getFeedObject() {
        return feedModel;
    }
}
