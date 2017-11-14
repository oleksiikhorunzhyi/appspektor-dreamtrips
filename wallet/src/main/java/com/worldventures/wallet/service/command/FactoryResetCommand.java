package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.service.FactoryResetInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.reset.ResetOptions;
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.util.WalletFeatureHelper;

import javax.inject.Inject;

import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.action.settings.RequestPinAuthAction;
import io.techery.janet.smartcard.event.PinStatusEvent;
import io.techery.janet.smartcard.event.PinStatusEvent.PinStatus;
import rx.Observable;
import rx.subjects.PublishSubject;

@CommandAction
public class FactoryResetCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject WalletFeatureHelper walletFeatureHelper;

   private final PublishSubject<Void> resetCommandPublishSubject;
   private final ResetOptions factoryResetOptions;

   public FactoryResetCommand(ResetOptions factoryResetOptions) {
      this.factoryResetOptions = factoryResetOptions;
      this.resetCommandPublishSubject = PublishSubject.create();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (factoryResetOptions.isWithEnterPin()) {
         Observable.merge(
               requestPinStatus()
                     .flatMap(status -> {
                        if (status == PinStatus.DISABLED || !walletFeatureHelper.pinFunctionalityAvailable()) {
                           // skip
                           return Observable.just(null);
                        } else {
                           // lock card and observe authorize
                           return requestPinEnteringAndReset();
                        }
                     }), resetCommandPublishSubject)
               .flatMap(aVoid -> resetSmartCard()) // reset action cannot be canceled, because it's not handled by hardware
               .subscribe(action -> callback.onSuccess(null), callback::onFail);
      } else {
         resetSmartCard().subscribe(action -> callback.onSuccess(null), callback::onFail);
      }
   }

   private Observable<ResetSmartCardCommand> resetSmartCard() {
      return factoryResetInteractor.resetSmartCardCommandActionPipe()
            .createObservableResult(new ResetSmartCardCommand(factoryResetOptions));
   }

   private Observable<PinStatusEvent.PinStatus> requestPinStatus() {
      return smartCardInteractor.checkPinStatusActionPipe()
            .createObservableResult(new CheckPinStatusAction())
            .flatMap(action -> smartCardInteractor.pinStatusEventPipe()
                  .observeSuccess()
                  .take(1)
                  .map(event -> event.pinStatus)
            );
   }

   private Observable<Void> observeAuthenticationAndReset() {
      return smartCardInteractor.pinStatusEventPipe()
            .observeSuccess()
            .filter(event -> event.pinStatus == PinStatus.AUTHENTICATED)
            .take(1)
            .map(pinStatusEvent -> null);
   }

   private Observable<Void> requestPinEnteringAndReset() {
      return smartCardInteractor.requestPinAuthActionPipe()
            .createObservableResult(new RequestPinAuthAction())
            .flatMap(action -> observeAuthenticationAndReset());
   }

   @Override
   protected void cancel() {
      resetCommandPublishSubject.onError(new CancelException());
   }
}
