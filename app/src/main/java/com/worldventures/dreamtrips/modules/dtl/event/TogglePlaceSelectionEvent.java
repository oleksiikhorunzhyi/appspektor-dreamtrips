package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class TogglePlaceSelectionEvent {
    private DtlPlace dtlPlace;

    public TogglePlaceSelectionEvent(DtlPlace dtlPlace) {
        this.dtlPlace = dtlPlace;
    }

    public DtlPlace getDtlPlace() {
        return dtlPlace;
    }
}
