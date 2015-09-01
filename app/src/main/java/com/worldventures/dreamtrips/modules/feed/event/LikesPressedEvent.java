package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;

public class LikesPressedEvent {

    BaseEventModel model;

    public LikesPressedEvent(BaseEventModel model) {
        this.model = model;
    }

    public BaseEventModel getModel() {
        return model;
    }
}
