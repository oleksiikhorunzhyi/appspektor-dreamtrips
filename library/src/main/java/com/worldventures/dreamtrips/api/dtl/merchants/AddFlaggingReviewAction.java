package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.SdkFlaggingResponse;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.SdkFlaggingReviewParams;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/review/v1/feedback?reviewId={merchant_id}", method = HttpAction.Method.POST)
public class AddFlaggingReviewAction extends AuthorizedHttpAction {

    @Path("merchant_id")
    String merchantId;

    @Body
    SdkFlaggingReviewParams flaggingParams;

    @Response
    SdkFlaggingResponse flaggingResponse;

    public AddFlaggingReviewAction(String merchantId, SdkFlaggingReviewParams flagging) {
        this.merchantId = merchantId;
        this.flaggingParams = flagging;
    }

    public SdkFlaggingResponse getFlaggingResponse() {
        return flaggingResponse;
    }
}
