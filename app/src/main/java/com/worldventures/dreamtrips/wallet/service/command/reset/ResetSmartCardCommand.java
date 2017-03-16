package com.worldventures.dreamtrips.wallet.service.command.reset;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import io.techery.janet.smartcard.exception.NotConnectedException;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class ResetSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   private final SmartCard smartCard;
   private final boolean withErasePaymentCardData;

   public ResetSmartCardCommand(SmartCard smartCard) {
      this(smartCard, true);
   }

   public ResetSmartCardCommand(SmartCard smartCard, boolean withErasePaymentCardData) {
      this.smartCard = smartCard;
      this.withErasePaymentCardData = withErasePaymentCardData;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      reset().subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> reset() {
      // TODO: 2/20/17
//      if (!smartCard.connectionStatus().isConnected()) return Observable.error(new NotConnectedException());

      return disableAutoLock()
            .flatMap(action -> wipeSmartCardData())
            .flatMap(action -> disconnect())
            .map(action -> null);
   }

   private Observable<WipeSmartCardDataCommand> wipeSmartCardData() {
      return walletJanet.createPipe(WipeSmartCardDataCommand.class)
            .createObservableResult(new WipeSmartCardDataCommand(withErasePaymentCardData));
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
