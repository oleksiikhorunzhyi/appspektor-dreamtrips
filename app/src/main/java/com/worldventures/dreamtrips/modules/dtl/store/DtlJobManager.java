package com.worldventures.dreamtrips.modules.dtl.store;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;

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

    public final Job3Executor<String, Integer, String, Void> rateExecutor =
            new Job3Executor<>(this::rate);

    public final Job3Executor<String, String, DtlTransaction, DtlTransactionResult> earnPointsExecutor =
            new Job3Executor<>(this::earnPoints);

    ///////////////////////////////////////////////////////////////////////////
    // Essential private methods
    ///////////////////////////////////////////////////////////////////////////

    private Observable<EstimationPointsHolder> estimatePoints(String merchantId, Double userInputValue,
                                                              String currencyCode) {
        return apiFactory.composeApiCall(() ->
                dtlApi.estimatePoints(merchantId, userInputValue, currencyCode,
                        DateTimeUtils.currentUtcString()));
    }

    private Observable<Void> rate(String merchantId, Integer stars, String transactionId) {
        return apiFactory.composeApiCall(() -> dtlApi.rate(merchantId, stars, transactionId));
    }

    private Observable<DtlTransactionResult> earnPoints(String merchantId, String currencyCode, DtlTransaction transactionData) {
        return apiFactory.composeApiCall(() -> dtlApi.earnPoints(merchantId, transactionData.asTransactionRequest(currencyCode)));
    }
}
