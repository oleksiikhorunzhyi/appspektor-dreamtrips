package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;

import io.techery.janet.http.annotations.Field;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

@HttpAction(value = "/api/dtl/v2/merchants/{id}/ratings",
        method = HttpAction.Method.POST,
        type = HttpAction.Type.FORM_URL_ENCODED)
public class DtlRateAction extends AuthorizedHttpAction {

    @Path("id")
    String merchantId;

    @Field("rating")
    Integer stars;

    @Field("transaction_id")
    String transactionId;

    public DtlRateAction(String merchantId, int stars, String transactionId) {
        this.merchantId = merchantId;
        this.stars = stars;
        this.transactionId = transactionId;
    }
}
