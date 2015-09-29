package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;

public class DeleteBucketEvent {

    private BucketFeedItem eventModel;

    public DeleteBucketEvent(BucketFeedItem eventModel) {
        this.eventModel = eventModel;
    }

    public BucketFeedItem getEventModel() {
        return eventModel;
    }
}
