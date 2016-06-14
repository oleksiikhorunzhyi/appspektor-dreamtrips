package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEstimatePointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlRateAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class DtlTransactionInteractor {

    private final ActionPipe<DtlEstimatePointsAction> estimatePointsActionPipe;
    private final ActionPipe<DtlRateAction> rateActionPipe;
    private final ActionPipe<DtlEarnPointsAction> earnPointsActionPipe;
    private final ActionPipe<DtlTransactionAction> transactionActionPipe;

    public DtlTransactionInteractor(Janet janet) {
        estimatePointsActionPipe = janet.createPipe(DtlEstimatePointsAction.class, Schedulers.io());
        rateActionPipe = janet.createPipe(DtlRateAction.class, Schedulers.io());
        earnPointsActionPipe = janet.createPipe(DtlEarnPointsAction.class, Schedulers.io());
        transactionActionPipe = janet.createPipe(DtlTransactionAction.class, Schedulers.io());
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

    public ActionPipe<DtlTransactionAction> transactionActionPipe() {
        return transactionActionPipe;
    }
}
