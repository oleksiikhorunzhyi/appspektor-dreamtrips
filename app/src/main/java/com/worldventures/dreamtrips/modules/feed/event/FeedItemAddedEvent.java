package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

public class FeedItemAddedEvent {
    BaseEventModel<IFeedObject> baseEventModel;

    public FeedItemAddedEvent(BaseEventModel<IFeedObject> baseEventModel) {
        this.baseEventModel = baseEventModel;
    }

    public BaseEventModel<IFeedObject> getBaseEventModel() {
        return baseEventModel;
    }
}
