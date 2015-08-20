package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;

public class CommentsPressedEvent {

    BaseEventModel model;

    public CommentsPressedEvent(BaseEventModel model) {
        this.model = model;
    }

    public BaseEventModel getModel() {
        return model;
    }
}
