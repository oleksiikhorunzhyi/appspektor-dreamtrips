package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;
import rx.schedulers.Schedulers;

public class FactoryResetInteractor {

   private final ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe;
   private final ActionPipe<LockDeviceAction> lockDevicePipe;

   public FactoryResetInteractor(Janet walletJanetInstance) {
      factoryResetCommandActionPipe = walletJanetInstance.createPipe(FactoryResetCommand.class, Schedulers.io());
      lockDevicePipe = walletJanetInstance.createPipe(LockDeviceAction.class, Schedulers.io());
   }

   public ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe() {
      return factoryResetCommandActionPipe;
   }

   public ActionPipe<LockDeviceAction> lockDevicePipe() {
      return lockDevicePipe;
   }

}
