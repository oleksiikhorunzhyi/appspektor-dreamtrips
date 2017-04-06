package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.TransactionDetails;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.Transaction;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/dtl/v2/merchants/{merchant_id}/transactions", method = HttpAction.Method.POST)
public class AddTransactionHttpAction extends AuthorizedHttpAction {

    @Path("merchant_id")
    String merchantId;
    @Body
    Transaction requestBody;

    @Response
    TransactionDetails response;

    public AddTransactionHttpAction(String merchantId, Transaction transaction) {
        this.requestBody = transaction;
        this.merchantId = merchantId;
    }

    public TransactionDetails transactionDetails() {
        return response;
    }
}
