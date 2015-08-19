package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

public class LikesPressedEvent {

    BaseFeedModel model;

    public LikesPressedEvent(BaseFeedModel model) {
        this.model = model;
    }

    public BaseFeedModel getModel() {
        return model;
    }
}
