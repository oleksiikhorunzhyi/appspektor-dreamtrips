package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

public class LocationClickedEvent {

    private DtlLocation location;

    public LocationClickedEvent() {
    }

    public LocationClickedEvent(DtlLocation location) {
        this.location = location;
    }

    public DtlLocation getLocation() {
        return location;
    }

    public void setLocation(DtlLocation location) {
        this.location = location;
    }
}
