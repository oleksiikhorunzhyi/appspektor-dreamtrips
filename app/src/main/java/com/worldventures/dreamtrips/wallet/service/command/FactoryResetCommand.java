package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.reset.ConfirmResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.event.LockDeviceChangedEvent;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class FactoryResetCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject @Named(JANET_WALLET) Janet walletJanet;

   private final PublishSubject<Void> resetCommandPublishSubject;
   private final boolean withEnterPin;

   public FactoryResetCommand(boolean withEnterPin) {
      this.withEnterPin = withEnterPin;
      this.resetCommandPublishSubject = PublishSubject.create();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (withEnterPin) {
         Observable.merge(
               observeConfirmReset()
                     .flatMap(confirmResetCommand -> observeUnlockCard())
                     .flatMap(lockDeviceChangedEvent -> resetSmartCard()),
               resetCommandPublishSubject).subscribe(action -> callback.onSuccess(null), callback::onFail);
      } else {
         resetSmartCard().subscribe(action -> callback.onSuccess(null), callback::onFail);
      }
   }

   private Observable<ResetSmartCardCommand> resetSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .flatMap(activeSmartCardCommand -> observeResetSmartCard(activeSmartCardCommand.getResult()));
   }

   private Observable<ConfirmResetCommand> observeConfirmReset() {
      return walletJanet.createPipe(ConfirmResetCommand.class, Schedulers.io()).createObservableResult(new ConfirmResetCommand());
   }

   private Observable<ResetSmartCardCommand> observeResetSmartCard(SmartCard smartCard) {
      return walletJanet.createPipe(ResetSmartCardCommand.class, Schedulers.io()).createObservableResult(new ResetSmartCardCommand(smartCard));
   }

   private Observable<LockDeviceChangedEvent> observeUnlockCard() {
      return walletJanet.createPipe(LockDeviceChangedEvent.class, Schedulers.io())
            .observeSuccess()
            .filter(lockDeviceChangedEvent -> !lockDeviceChangedEvent.locked)
            .take(1);
   }

   @Override
   protected void cancel() {
      resetCommandPublishSubject.onError(new CancelException());
   }
}
