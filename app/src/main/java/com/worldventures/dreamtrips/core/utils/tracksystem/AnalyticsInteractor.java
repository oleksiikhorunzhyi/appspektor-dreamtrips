package com.worldventures.dreamtrips.core.utils.tracksystem;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.utils.tracksystem.command.ClearHeadersCommand;
import com.worldventures.dreamtrips.core.utils.tracksystem.command.SetUserIdsHeadersCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class AnalyticsInteractor {

   private final ActionPipe<BaseAnalyticsAction> analyticsActionPipe;
   private final ActionPipe<DtlAnalyticsCommand> analyticsCommandPipe;
   private final ActionPipe<ClearHeadersCommand> clearAdobeHeadersPipe;
   private final ActionPipe<SetUserIdsHeadersCommand> setAdobeUserIdsPipe;

   public AnalyticsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      analyticsActionPipe = sessionActionPipeCreator.createPipe(BaseAnalyticsAction.class, Schedulers.io());
      analyticsCommandPipe = sessionActionPipeCreator.createPipe(DtlAnalyticsCommand.class, Schedulers.io());
      clearAdobeHeadersPipe = sessionActionPipeCreator.createPipe(ClearHeadersCommand.class, Schedulers.io());
      setAdobeUserIdsPipe = sessionActionPipeCreator.createPipe(SetUserIdsHeadersCommand.class, Schedulers.io());
   }

   public WriteActionPipe<BaseAnalyticsAction> analyticsActionPipe() {
      return analyticsActionPipe;
   }

   public WriteActionPipe<DtlAnalyticsCommand> dtlAnalyticsCommandPipe() {
      return analyticsCommandPipe;
   }

   public ActionPipe<ClearHeadersCommand> clearAdobeHeadersPipe() {
      return clearAdobeHeadersPipe;
   }

   public ActionPipe<SetUserIdsHeadersCommand> setUserIdsPipe() {
      return setAdobeUserIdsPipe;
   }
}
