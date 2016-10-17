package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FactoryResetManager {

   private final ActionPipe<ResetSmartCardCommand> resetSmartCardPipe;

   public FactoryResetManager(Janet walletJanetInstance) {
      resetSmartCardPipe = walletJanetInstance.createPipe(ResetSmartCardCommand.class, Schedulers.io());
   }

   public void factoryReset() {
      //todo: remove for not use lock/unlock for current logic
      resetSmartCardPipe.send(new ResetSmartCardCommand());
   }

   public Observable<ActionState<ResetSmartCardCommand>> observeFactoryResetPipe() {
      return resetSmartCardPipe.createObservable(new ResetSmartCardCommand());
   }
}
