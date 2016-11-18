package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.reset.ConfirmResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FactoryResetManager {

   private final ActionPipe<ResetSmartCardCommand> resetSmartCardPipe;
   private final ActionPipe<ConfirmResetCommand> confirmResetPipe;
   private final SmartCardInteractor smartCardInteractor;

   public FactoryResetManager(Janet walletJanetInstance, SmartCardInteractor smartCardInteractor) {
      this.smartCardInteractor = smartCardInteractor;
      resetSmartCardPipe = walletJanetInstance.createPipe(ResetSmartCardCommand.class, Schedulers.io());
      confirmResetPipe = walletJanetInstance.createPipe(ConfirmResetCommand.class, Schedulers.io());
   }

   public void factoryReset() {
      confirmResetPipe.send(new ConfirmResetCommand());
   }

   public Observable<ActionState<ResetSmartCardCommand>> observeFactoryResetPipe() {
      return smartCardInteractor.lockDeviceChangedEventPipe()
            .observeSuccess()
            .filter(event -> !event.locked)
            .take(1)
            .flatMap(event -> resetSmartCardPipe.createObservable(new ResetSmartCardCommand()));
   }
}
