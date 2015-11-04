package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class PlaceClickedEvent {

    private final DtlPlace place;

    public PlaceClickedEvent(DtlPlace place) {
        this.place = place;
    }

    public DtlPlace getPlace() {
        return place;
    }
}
