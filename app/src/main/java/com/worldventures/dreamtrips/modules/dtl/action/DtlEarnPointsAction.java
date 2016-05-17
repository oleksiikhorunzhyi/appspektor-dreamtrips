package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/dtl/v2/merchants/{id}/transactions", method = HttpAction.Method.POST)
public class DtlEarnPointsAction extends AuthorizedHttpAction {

    @Path("id")
    String merchantId;

    @Body
    DtlTransaction.Request request;

    @Response
    DtlTransactionResult result;

    public DtlEarnPointsAction(String merchantId, DtlTransaction.Request request) {
        this.merchantId = merchantId;
        this.request = request;
    }

    public DtlTransactionResult getResult() {
        return result;
    }
}
