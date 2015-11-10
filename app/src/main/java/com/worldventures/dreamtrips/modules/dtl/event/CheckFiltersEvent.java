package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

public class CheckFiltersEvent {
    DtlLocation dtlLocation;

    public CheckFiltersEvent(DtlLocation dtlLocation) {
        this.dtlLocation = dtlLocation;
    }

    public DtlLocation getDtlLocation() {
        return dtlLocation;
    }
}
