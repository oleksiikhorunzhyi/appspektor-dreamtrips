package com.worldventures.dreamtrips.core.utils.tracksystem;

import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class AnalyticsInteractor {

    private final ActionPipe<BaseAnalyticsAction> analyticEventPipe;
    private final ActionPipe<DtlAnalyticsCommand> dtlAnalyticCommandPipe;

    public AnalyticsInteractor(Janet janet) {
        analyticEventPipe = janet.createPipe(BaseAnalyticsAction.class, Schedulers.io());
        dtlAnalyticCommandPipe = janet.createPipe(DtlAnalyticsCommand.class, Schedulers.io());
    }

    public WriteActionPipe<BaseAnalyticsAction> analyticsActionPipe() {
        return analyticEventPipe;
    }

    public WriteActionPipe<DtlAnalyticsCommand> dtlAnalyticsCommandPipe() {
        return dtlAnalyticCommandPipe;
    }
}