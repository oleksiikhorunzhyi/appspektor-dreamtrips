package com.worldventures.dreamtrips.modules.dtl.store;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlEstimatePointsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlRateAction;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class DtlJobManager {

    @Inject
    Janet janet;

    public final ActionPipe<DtlEstimatePointsAction> estimatePointsActionPipe;

    public final ActionPipe<DtlRateAction> rateActionPipe;

    public final ActionPipe<DtlEarnPointsAction> earnPointsActionPipe;

    public DtlJobManager(Injector injector) {
        injector.inject(this);
        estimatePointsActionPipe = janet.createPipe(DtlEstimatePointsAction.class, Schedulers.io());
        rateActionPipe = janet.createPipe(DtlRateAction.class, Schedulers.io());
        earnPointsActionPipe = janet.createPipe(DtlEarnPointsAction.class, Schedulers.io());
    }
}
