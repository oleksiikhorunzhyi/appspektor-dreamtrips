package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class LikesPressedEvent {

    FeedItem model;

    public LikesPressedEvent(FeedItem model) {
        this.model = model;
    }

    public FeedItem getModel() {
        return model;
    }
}
