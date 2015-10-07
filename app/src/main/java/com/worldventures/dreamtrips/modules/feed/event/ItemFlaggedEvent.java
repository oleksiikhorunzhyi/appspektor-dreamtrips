package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.UidItem;

public class ItemFlaggedEvent {

    private UidItem entity;

    private String nameOfReason;

    public ItemFlaggedEvent(UidItem entity, String nameOfReason) {
        this.entity = entity;
        this.nameOfReason = nameOfReason;
    }

    public UidItem getEntity() {
        return entity;
    }

    public String getNameOfReason() {
        return nameOfReason;
    }
}
