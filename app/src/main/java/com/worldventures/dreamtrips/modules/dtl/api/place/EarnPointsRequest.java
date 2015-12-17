package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;

public class EarnPointsRequest extends DtlRequest<DtlTransactionResult> {

    private String id;
    private String currencyCode;
    private DtlTransaction request;

    public EarnPointsRequest(String id, String currencyCode, DtlTransaction request) {
        super(DtlTransactionResult.class);
        this.id = id;
        this.request = request;
        this.currencyCode = currencyCode;
    }

    @Override
    public DtlTransactionResult loadDataFromNetwork() throws Exception {
        return getService().earnPoints(id, request.asTransactionRequest(currencyCode));
    }


}
