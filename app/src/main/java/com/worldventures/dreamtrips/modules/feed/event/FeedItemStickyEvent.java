package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

public class FeedItemStickyEvent {

    BaseFeedModel model;

    public FeedItemStickyEvent(BaseFeedModel model) {
        this.model = model;
    }

    public BaseFeedModel getModel() {
        return model;
    }
}
