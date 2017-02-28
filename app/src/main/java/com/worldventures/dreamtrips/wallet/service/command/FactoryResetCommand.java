package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;
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
   private final boolean withErasePaymentCardData;

   public FactoryResetCommand(boolean withEnterPin) {
      this(withEnterPin, true);
   }

   public FactoryResetCommand(boolean withEnterPin, boolean withErasePaymentCardData) {
      this.withEnterPin = withEnterPin;
      this.withErasePaymentCardData = withErasePaymentCardData;
      this.resetCommandPublishSubject = PublishSubject.create();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (withEnterPin) {
         Observable.merge(
               walletJanet.createPipe(GetLockDeviceStatusAction.class)
                     .createObservableResult(new GetLockDeviceStatusAction())
                     .flatMap(action -> lockObservable(action.locked))
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

   private Observable<Void> lockObservable(boolean isLock) {
      if (!isLock) {
         return walletJanet.createPipe(LockDeviceAction.class, Schedulers.io())
               .createObservableResult(new LockDeviceAction(true))
               .map(lockDeviceAction -> null);
      }
      return Observable.just(null);
   }

   private Observable<ResetSmartCardCommand> observeResetSmartCard(SmartCard smartCard) {
      return walletJanet.createPipe(ResetSmartCardCommand.class, Schedulers.io())
            .createObservableResult(new ResetSmartCardCommand(smartCard, withErasePaymentCardData));
   }

   private Observable<LockDeviceChangedEvent> observeUnlockCard() {
      return smartCardInteractor.lockDeviceChangedEventPipe()
            .observeSuccess()
            .filter(event -> !event.locked)
            .take(1);
   }

   @Override
   protected void cancel() {
      resetCommandPublishSubject.onError(new CancelException());
   }
}
