package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;

public class DeletePhotoEvent {

    public PhotoFeedItem feedItem;

    public DeletePhotoEvent(PhotoFeedItem feedItem) {
        this.feedItem = feedItem;
    }

    public FeedItem getEntity() {
        return feedItem;
    }
}
