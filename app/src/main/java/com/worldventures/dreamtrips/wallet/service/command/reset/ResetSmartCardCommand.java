package com.worldventures.dreamtrips.wallet.service.command.reset;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.JanetActionException;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import io.techery.janet.smartcard.action.user.UnAssignUserAction;
import io.techery.janet.smartcard.exception.NotConnectedException;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class ResetSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet apiLibJanet;
   @Inject @Named(JANET_WALLET) Janet walletJanet;
   private final SmartCard smartCard;

   public ResetSmartCardCommand(SmartCard smartCard) {
      this.smartCard = smartCard;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      reset().subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> reset() {
      if (!smartCard.connectionStatus().isConnected()) return Observable.error(new NotConnectedException());

      return disableAutoLock()
            .flatMap(action -> disassociateCardUserServer(smartCard))
            .flatMap(action -> disassociateCardUser())
            .flatMap(action -> disconnect())
            .flatMap(action -> removeSmartCardData())
            .map(action -> null);
   }

   private Observable<EnableLockUnlockDeviceAction> disableAutoLock() {
      return walletJanet.createPipe(EnableLockUnlockDeviceAction.class)
            .createObservableResult(new EnableLockUnlockDeviceAction(false))
            .onErrorResumeNext(Observable.just(null));
   }

   private Observable<Void> disassociateCardUserServer(SmartCard smartCard) {
      return apiLibJanet.createPipe(DisassociateCardUserHttpAction.class, Schedulers.io())
            .createObservableResult(
                  new DisassociateCardUserHttpAction(Long.parseLong(smartCard.smartCardId()), smartCard.deviceId()))
            .map(disassociateCardUserHttpAction -> (Void) null)
            .onErrorResumeNext(throwable -> {
               JanetActionException actionException = (JanetActionException) throwable;
               if (((BaseHttpAction) actionException.getAction()).statusCode() == 404) {
                  return Observable.just(null);
               } else {
                  return Observable.error(throwable);
               }
            });
   }

   private Observable<UnAssignUserAction> disassociateCardUser() {
      return walletJanet.createPipe(UnAssignUserAction.class)
            .createObservableResult(new UnAssignUserAction());
   }

   private Observable<DisconnectAction> disconnect() {
      return walletJanet.createPipe(DisconnectAction.class)
            .createObservableResult(new DisconnectAction());
   }

   private Observable<RemoveSmartCardDataCommand> removeSmartCardData() {
      return walletJanet.createPipe(RemoveSmartCardDataCommand.class)
            .createObservableResult(new RemoveSmartCardDataCommand());
   }
}
