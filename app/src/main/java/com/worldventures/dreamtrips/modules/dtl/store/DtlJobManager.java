package com.worldventures.dreamtrips.modules.dtl.store;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;

import javax.inject.Inject;

import rx.Observable;
import techery.io.library.Job3Executor;

public class DtlJobManager {

    @Inject
    DtlApi dtlApi;
    @Inject
    RxApiFactory apiFactory;

    public DtlJobManager(Injector injector) {
        injector.inject(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Executors
    ///////////////////////////////////////////////////////////////////////////

    public final Job3Executor<String, Double, String, EstimationPointsHolder> estimatePointsExecutor =
            new Job3Executor<>(this::estimatePoints);

    ///////////////////////////////////////////////////////////////////////////
    // Essential private methods
    ///////////////////////////////////////////////////////////////////////////

    private Observable<EstimationPointsHolder> estimatePoints(String merchantId, Double userInputValue,
                                                              String currencyCode) {
        return apiFactory.composeApiCall(() ->
                dtlApi.estimatePoints(merchantId, userInputValue, currencyCode,
                        DateTimeUtils.currentUtcString()));
    }
}
