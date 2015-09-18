package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;

public class DeleteBucketEvent {

    private FeedBucketEventModel eventModel;

    public DeleteBucketEvent(FeedBucketEventModel eventModel) {
        this.eventModel = eventModel;
    }

    public FeedBucketEventModel getEventModel() {
        return eventModel;
    }
}
