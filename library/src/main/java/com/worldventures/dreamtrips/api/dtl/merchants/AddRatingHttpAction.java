package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RatingParams;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

@HttpAction(value = "api/dtl/v2/merchants/{merchant_id}/ratings", method = HttpAction.Method.POST)
public class AddRatingHttpAction extends AuthorizedHttpAction {

    @Path("merchant_id")
    public final String merchantId;

    @Body
    public final RatingParams ratingParams;

    public AddRatingHttpAction(String merchantId, RatingParams params) {
        this.merchantId = merchantId;
        this.ratingParams = params;
    }
}
