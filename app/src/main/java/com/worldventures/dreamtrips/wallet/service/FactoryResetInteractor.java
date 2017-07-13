package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FactoryResetInteractor {

   private final ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe;
   private final ActionPipe<ResetSmartCardCommand> resetSmartCardCommandActionPipe;

   public FactoryResetInteractor(SessionActionPipeCreator pipeCreator) {
      factoryResetCommandActionPipe = pipeCreator.createPipe(FactoryResetCommand.class, Schedulers.io());
      resetSmartCardCommandActionPipe = pipeCreator.createPipe(ResetSmartCardCommand.class, Schedulers.io());
   }

   public ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe() {
      return factoryResetCommandActionPipe;
   }

   public ActionPipe<ResetSmartCardCommand> resetSmartCardCommandActionPipe() {
      return resetSmartCardCommandActionPipe;
   }
}
