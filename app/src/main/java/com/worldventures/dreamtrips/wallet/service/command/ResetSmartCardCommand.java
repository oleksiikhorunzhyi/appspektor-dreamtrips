package com.worldventures.dreamtrips.wallet.service.command;


import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;
import io.techery.janet.smartcard.action.user.UnAssignUserAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class ResetSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet apiLibJanet;
   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private SmartCard smartCard;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .doOnNext(cardCommand -> smartCard = cardCommand.getResult())
            .flatMap(cardCommand -> lockUnlockSmartCard())
            .flatMap(setLockStateCommand -> disassociateCardUserServer())
            .flatMap(disassociateCardUserHttpAction -> disassociateCardUser())
            .flatMap(unAssignUserAction -> removeSmartCardData())
            .subscribe(removeSmartCardDataCommand -> callback.onSuccess(null), callback::onFail);
   }

   //before unassisign user, he should enter pin. For this purpose smartCard should be locked and unlocked,
   // because unlock operation performs by entering pin.
   private Observable<LockDeviceAction> lockUnlockSmartCard() {
      ActionPipe<LockDeviceAction> lockDevicePipe = janet.createPipe(LockDeviceAction.class);

      if (smartCard.lock()) {
         return lockDevicePipe
               .createObservableResult(new LockDeviceAction(false));
      } else {
         return lockDevicePipe
               .createObservableResult(new LockDeviceAction(true))
               .flatMap(setLockStateCommand -> lockDevicePipe.createObservableResult(new LockDeviceAction(false)));
      }
   }

   private Observable<DisassociateCardUserHttpAction> disassociateCardUserServer() {
      long scId = Long.parseLong(smartCard.smartCardId());
      return apiLibJanet.createPipe(DisassociateCardUserHttpAction.class)
            .createObservableResult(new DisassociateCardUserHttpAction(scId));
   }

   private Observable<UnAssignUserAction> disassociateCardUser() {
      return janet.createPipe(UnAssignUserAction.class)
            .createObservableResult(new UnAssignUserAction());
   }

   private Observable<RemoveSmartCardDataCommand> removeSmartCardData() {
      return janet.createPipe(RemoveSmartCardDataCommand.class)
            .createObservableResult(new RemoveSmartCardDataCommand(smartCard.smartCardId()));
   }

}
