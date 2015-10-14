package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;

public class DtlFilterEvent {

    DtlFilterData dtlFilterData;

    public DtlFilterEvent(DtlFilterData dtlFilterData) {
        this.dtlFilterData = dtlFilterData;
    }

    public DtlFilterData getDtlFilterData() {
        return dtlFilterData;
    }
}
