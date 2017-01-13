package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class FactoryResetInteractor {

   private final ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe;

   public FactoryResetInteractor(Janet walletJanetInstance) {
      factoryResetCommandActionPipe = walletJanetInstance.createPipe(FactoryResetCommand.class, Schedulers.io());
   }

   public ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe() {
      return factoryResetCommandActionPipe;
   }
}
