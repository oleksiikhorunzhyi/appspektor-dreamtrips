package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

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
                        if (status == PinStatus.DISABLED) {
                           // skip
                           return Observable.just(null);
                        } else if (status == PinStatus.NEED_AUTHENTICATION || status == PinStatus.USER_INTERFACING) {
                           // observe authorize
                           return observeAuthenticationAndReset();
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
