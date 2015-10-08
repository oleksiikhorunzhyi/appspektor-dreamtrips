package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

public class PlacesUpdatedEvent {

    private DtlPlaceType type;
    private boolean endedWithError = false;

    public PlacesUpdatedEvent(DtlPlaceType type) {
        this.type = type;
    }

    public PlacesUpdatedEvent(boolean endedWithError) {
        this.endedWithError = endedWithError;
    }

    public DtlPlaceType getType() {
        return type;
    }

    public void setType(DtlPlaceType type) {
        this.type = type;
    }

    public boolean isEndedWithError() {
        return endedWithError;
    }

    public void setEndedWithError(boolean endedWithError) {
        this.endedWithError = endedWithError;
    }
}
