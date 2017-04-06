package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.EstimationResult;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.EstimationParams;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/dtl/v2/merchants/{merchant_id}/estimations", method = HttpAction.Method.POST)
public class EstimatePointsHttpAction extends AuthorizedHttpAction {

    @Path("merchant_id")
    public final String merchantId;

    @Body
    public final EstimationParams estimationParams;

    @Response
    EstimationResult estimationResult;

    public EstimatePointsHttpAction(String merchantId, EstimationParams params) {
        this.merchantId = merchantId;
        this.estimationParams = params;
    }

    public EstimationResult estimatedPoints() {
        return estimationResult;
    }
}
