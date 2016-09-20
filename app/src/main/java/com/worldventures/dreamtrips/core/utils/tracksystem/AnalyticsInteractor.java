package com.worldventures.dreamtrips.core.utils.tracksystem;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class AnalyticsInteractor {

   private final ActionPipe<BaseAnalyticsAction> analyticEventPipe;
   private final ActionPipe<DtlAnalyticsCommand> dtlAnalyticCommandPipe;

   public AnalyticsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      analyticEventPipe = sessionActionPipeCreator.createPipe(BaseAnalyticsAction.class, Schedulers.io());
      dtlAnalyticCommandPipe = sessionActionPipeCreator.createPipe(DtlAnalyticsCommand.class, Schedulers.io());
   }

   public WriteActionPipe<BaseAnalyticsAction> analyticsActionPipe() {
      return analyticEventPipe;
   }

   public WriteActionPipe<DtlAnalyticsCommand> dtlAnalyticsCommandPipe() {
      return dtlAnalyticCommandPipe;
   }
}
