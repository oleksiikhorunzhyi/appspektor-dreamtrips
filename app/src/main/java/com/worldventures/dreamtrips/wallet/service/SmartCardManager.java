package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardModifier;
import com.worldventures.dreamtrips.wallet.service.command.UpdateSmartCardPropertiesCommand;

import java.util.concurrent.TimeUnit;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import io.techery.janet.smartcard.action.user.UnAssignUserAction;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class SmartCardManager {

   private final BehaviorSubject<SmartCard> subject = BehaviorSubject.create();
   private final Janet janet;
   private final SmartCardInteractor smartCardInteractor;

   public SmartCardManager(Janet janet, SmartCardInteractor smartCardInteractor) {
      this.janet = janet;
      this.smartCardInteractor = smartCardInteractor;
      observeConnecting();

      Observable.merge(
            smartCardInteractor.smartCardModifierPipe().observeSuccess().map(SmartCardModifier::getResult),
            smartCardInteractor.activeSmartCardPipe().observeSuccess().map(Command::getResult)
      ).subscribe(subject::onNext);
   }

   public Observable<SmartCard> smartCardObservable() {
      if (subject.hasValue()) {
         smartCardInteractor.activeSmartCardPipe().send(new GetActiveSmartCardCommand());
      }
      return subject.asObservable();
   }

   public Observable<SmartCard> singleSmartCardObservable() {
      return smartCardObservable().take(1);
   }

   private void observeConnecting() {
      janet.createPipe(ConnectAction.class)
            .observeSuccess()
            .filter(connectAction -> connectAction.type == ConnectionType.APP)
            .subscribe(action -> smartCardConnected());
   }

   private void smartCardConnected() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(GetActiveSmartCardCommand::getResult)
            .filter(smartCard -> smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE)
            .subscribe(smartCard -> {
               createBatteryObservable();
               fetchFirmwareVersion();
            }, throwable -> {
            });
   }

   private void createBatteryObservable() {
      Observable.interval(0, 1, TimeUnit.MINUTES)
            .takeUntil(janet.createPipe(UnAssignUserAction.class).observe().first())
            .takeUntil(janet.createPipe(DisconnectAction.class).observeSuccess())
            .takeUntil(janet.createPipe(ConnectAction.class)
                  .observeSuccess()
                  .filter(action -> action.type == ConnectionType.DFU))
            .doOnNext(o -> janet.createPipe(FetchBatteryLevelCommand.class).send(new FetchBatteryLevelCommand()))
            .subscribe();
   }

   private void fetchFirmwareVersion() {
      janet.createPipe(UpdateSmartCardPropertiesCommand.class, Schedulers.io())
            .send(new UpdateSmartCardPropertiesCommand());
   }
}
