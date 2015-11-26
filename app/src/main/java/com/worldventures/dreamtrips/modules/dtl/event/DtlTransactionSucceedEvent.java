package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;

public class DtlTransactionSucceedEvent {

    private DtlTransaction dtlTransaction;

    public DtlTransactionSucceedEvent(DtlTransaction dtlTransaction) {
        this.dtlTransaction = dtlTransaction;
    }

    public DtlTransaction getDtlTransaction() {
        return dtlTransaction;
    }
}
