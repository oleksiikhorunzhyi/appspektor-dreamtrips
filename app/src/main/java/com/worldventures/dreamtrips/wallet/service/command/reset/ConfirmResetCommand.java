package com.worldventures.dreamtrips.wallet.service.command.reset;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class ConfirmResetCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject @Named(JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .flatMap(command -> lockUnlockSmartCard(command.getResult()))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }

   //before unassisign user, he should enter pin. For this purpose smartCard should be locked and unlocked,
   // because unlock operation performs by entering pin.
   private Observable<Void> lockUnlockSmartCard(SmartCard smartCard) {
      ActionPipe<LockDeviceAction> lockDevicePipe = janet.createPipe(LockDeviceAction.class);
      if (!smartCard.lock()) {
         return lockDevicePipe
               .createObservableResult(new LockDeviceAction(true))
               .map(lockDeviceAction -> null);
      }
      return Observable.just(null);
   }

}
