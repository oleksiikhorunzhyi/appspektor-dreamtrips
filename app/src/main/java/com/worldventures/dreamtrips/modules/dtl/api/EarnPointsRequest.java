package com.worldventures.dreamtrips.modules.dtl.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;

public class EarnPointsRequest extends DtlRequest<DtlTransactionResult> {

    private int id;
    private DtlTransaction request;

    public EarnPointsRequest(int id, DtlTransaction request) {
        super(DtlTransactionResult.class);
        this.id = id;
        this.request = request;
    }

    @Override
    public DtlTransactionResult loadDataFromNetwork() throws Exception {
        return getService().earnPoints(id, request);
    }


}
