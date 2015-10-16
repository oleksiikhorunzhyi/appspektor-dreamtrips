package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class CommentsPressedEvent {

    FeedItem model;

    public CommentsPressedEvent(FeedItem model) {
        this.model = model;
    }

    public FeedItem getModel() {
        return model;
    }
}
