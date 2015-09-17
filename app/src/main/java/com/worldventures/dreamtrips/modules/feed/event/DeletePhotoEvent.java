package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;

public class DeletePhotoEvent {

    public FeedPhotoEventModel feedItem;

    public DeletePhotoEvent(FeedPhotoEventModel feedItem) {
        this.feedItem = feedItem;
    }

    public BaseEventModel getEntity() {
        return feedItem;
    }
}
