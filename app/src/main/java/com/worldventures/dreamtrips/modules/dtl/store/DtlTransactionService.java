package com.worldventures.dreamtrips.modules.dtl.store;

import com.worldventures.dreamtrips.modules.dtl.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlEstimatePointsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlRateAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class DtlTransactionService {

    private final ActionPipe<DtlEstimatePointsAction> estimatePointsActionPipe;
    private final ActionPipe<DtlRateAction> rateActionPipe;
    private final ActionPipe<DtlEarnPointsAction> earnPointsActionPipe;

    public DtlTransactionService(Janet janet) {
        estimatePointsActionPipe = janet.createPipe(DtlEstimatePointsAction.class, Schedulers.io());
        rateActionPipe = janet.createPipe(DtlRateAction.class, Schedulers.io());
        earnPointsActionPipe = janet.createPipe(DtlEarnPointsAction.class, Schedulers.io());
    }

    public ActionPipe<DtlEstimatePointsAction> estimatePointsActionPipe() {
        return estimatePointsActionPipe;
    }

    public ActionPipe<DtlRateAction> rateActionPipe() {
        return rateActionPipe;
    }

    public ActionPipe<DtlEarnPointsAction> earnPointsActionPipe() {
        return earnPointsActionPipe;
    }
}
