package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import io.techery.janet.http.annotations.Field;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/dtl/v2/merchants/{id}/estimations",
        method = HttpAction.Method.POST,
        type = HttpAction.Type.FORM_URL_ENCODED)
public class DtlEstimatePointsAction extends AuthorizedHttpAction {

    @Path("id")
    String merchantId;

    @Field("bill_total")
    Double price;

    @Field("currency_code")
    String currencyCode;

    @Field("checkin_time")
    String checkinTime;

    @Response
    EstimationPointsHolder estimationPointsHolder;

    public DtlEstimatePointsAction(DtlMerchant merchant, Double price, String currencyCode) {
        this.merchantId = merchant.getId();
        this.price = price;
        this.currencyCode = currencyCode;
        this.checkinTime = DateTimeUtils.currentUtcString();
    }

    public EstimationPointsHolder getEstimationPointsHolder() {
        return estimationPointsHolder;
    }
}
