package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

public class CommentsPressedEvent {

    BaseFeedModel model;

    public CommentsPressedEvent(BaseFeedModel model) {
        this.model = model;
    }

    public BaseFeedModel getModel() {
        return model;
    }
}
