package com.worldventures.core.service.analytics;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.service.analytics.command.ClearHeadersCommand;
import com.worldventures.core.service.analytics.command.SetUserIdsHeadersCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class AnalyticsInteractor {

   private final ActionPipe<BaseAnalyticsAction> analyticsActionPipe;
   private final ActionPipe<Command> analyticsCommandPipe;
   private final ActionPipe<ClearHeadersCommand> clearAdobeHeadersPipe;
   private final ActionPipe<SetUserIdsHeadersCommand> setAdobeUserIdsPipe;

   public AnalyticsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      analyticsActionPipe = sessionActionPipeCreator.createPipe(BaseAnalyticsAction.class, Schedulers.io());
      analyticsCommandPipe = sessionActionPipeCreator.createPipe(Command.class, Schedulers.io());
      clearAdobeHeadersPipe = sessionActionPipeCreator.createPipe(ClearHeadersCommand.class, Schedulers.io());
      setAdobeUserIdsPipe = sessionActionPipeCreator.createPipe(SetUserIdsHeadersCommand.class, Schedulers.io());
   }

   public WriteActionPipe<BaseAnalyticsAction> analyticsActionPipe() {
      return analyticsActionPipe;
   }

   public WriteActionPipe<Command> analyticsCommandPipe() {
      return analyticsCommandPipe;
   }

   // todo check it
   public ActionPipe<ClearHeadersCommand> clearAdobeHeadersPipe() {
      return clearAdobeHeadersPipe;
   }

   public ActionPipe<SetUserIdsHeadersCommand> setUserIdsPipe() {
      return setAdobeUserIdsPipe;
   }
}
