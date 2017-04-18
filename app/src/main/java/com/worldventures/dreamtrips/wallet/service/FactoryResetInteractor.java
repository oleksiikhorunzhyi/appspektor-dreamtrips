package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;
import rx.schedulers.Schedulers;

public class FactoryResetInteractor {

   private final ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe;
   private final ActionPipe<ResetSmartCardCommand> resetSmartCardCommandActionPipe;
   private final ActionPipe<LockDeviceAction> lockDevicePipe;

   public FactoryResetInteractor(Janet walletJanetInstance) {
      factoryResetCommandActionPipe = walletJanetInstance.createPipe(FactoryResetCommand.class, Schedulers.io());
      resetSmartCardCommandActionPipe = walletJanetInstance.createPipe(ResetSmartCardCommand.class, Schedulers.io());
      lockDevicePipe = walletJanetInstance.createPipe(LockDeviceAction.class, Schedulers.io());
   }

   public ActionPipe<FactoryResetCommand> factoryResetCommandActionPipe() {
      return factoryResetCommandActionPipe;
   }

   public ActionPipe<ResetSmartCardCommand> resetSmartCardCommandActionPipe() {
      return resetSmartCardCommandActionPipe;
   }

   public ActionPipe<LockDeviceAction> lockDevicePipe() {
      return lockDevicePipe;
   }
}
