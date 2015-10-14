package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterObject;

public class DtlFilterEvent {

    DtlFilterObject dtlFilterObject;

    public DtlFilterEvent(DtlFilterObject dtlFilterObject) {
        this.dtlFilterObject = dtlFilterObject;
    }

    public DtlFilterObject getDtlFilterObject() {
        return dtlFilterObject;
    }
}
