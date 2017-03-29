package com.worldventures.dreamtrips.wallet.service.command.reset;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class ResetSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   private final ResetOptions factoryResetOptions;

   public ResetSmartCardCommand() {
      this(ResetOptions.builder().build());
   }

   public ResetSmartCardCommand(ResetOptions factoryResetOptions) {
      this.factoryResetOptions = factoryResetOptions;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      reset().subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> reset() {
      return disableAutoLock()
            .flatMap(action -> wipeSmartCardData())
            .flatMap(action -> disconnect())
            .map(action -> null);
   }

   private Observable<WipeSmartCardDataCommand> wipeSmartCardData() {
      return walletJanet.createPipe(WipeSmartCardDataCommand.class)
            .createObservableResult(new WipeSmartCardDataCommand(factoryResetOptions));
   }

   private Observable<EnableLockUnlockDeviceAction> disableAutoLock() {
      return walletJanet.createPipe(EnableLockUnlockDeviceAction.class)
            .createObservableResult(new EnableLockUnlockDeviceAction(false))
            .onErrorResumeNext(Observable.just(null));
   }

   private Observable<DisconnectAction> disconnect() {
      return walletJanet.createPipe(DisconnectAction.class)
            .createObservableResult(new DisconnectAction());
   }
}
