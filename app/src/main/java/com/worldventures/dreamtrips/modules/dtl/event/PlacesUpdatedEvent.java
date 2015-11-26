package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

public class PlacesUpdatedEvent {

    private DtlPlaceType type;

    public PlacesUpdatedEvent(DtlPlaceType type) {
        this.type = type;
    }

    public DtlPlaceType getType() {
        return type;
    }

    public void setType(DtlPlaceType type) {
        this.type = type;
    }
}
