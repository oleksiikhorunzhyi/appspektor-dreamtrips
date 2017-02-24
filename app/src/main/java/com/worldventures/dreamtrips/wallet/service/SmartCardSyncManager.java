package com.worldventures.dreamtrips.wallet.service;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.LoadFirmwareFilesCommand;

import java.util.concurrent.TimeUnit;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.DFU;
import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.DISCONNECTED;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.add;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.edit;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.remove;
import static java.lang.String.valueOf;

public class SmartCardSyncManager {

   private final Janet janet;
   private final SmartCardInteractor interactor;
   private volatile boolean syncDisabled = false;

   private CompositeSubscription subscriptions;

   public SmartCardSyncManager(Janet janet, SmartCardInteractor interactor) {
      this.janet = janet;
      this.interactor = interactor;

      subscriptions = new CompositeSubscription();
   }

   public void connect() {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed()) {
         observeConnection();
         connectUpdateSmartCard();
         connectSyncCards();
         connectSyncDisabling();
      }
   }

   public void disconnect() {
      if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
         subscriptions.clear();
      }
   }

   private void connectSyncDisabling() {
      subscriptions.add(janet.createPipe(LoadFirmwareFilesCommand.class)
            .observeWithReplay()
            .subscribe(new ActionStateSubscriber<LoadFirmwareFilesCommand>()
                  .onStart(loadFirmwareFilesCommand -> syncDisabled = true)
                  .onFinish(loadFirmwareFilesCommand -> syncDisabled = false)));
   }

   private void observeConnection() {
      // // TODO: 2/20/17 create pipe in interactor
      subscriptions.add(janet.createPipe(ConnectAction.class)
            .observeSuccess()
            .throttleLast(1, TimeUnit.SECONDS)
            .subscribe(connectAction -> cardConnected(connectAction.type == ConnectionType.DFU ? DFU : CONNECTED),
                  throwable -> Timber.e(throwable, "Error with handling connection event")));

      subscriptions.add(interactor.disconnectPipe()
            .observeSuccess()
            .subscribe(command -> cardDisconnected(), throwable -> Timber.e(throwable, "Error while updating status of active card")));
   }

   private void cardConnected(ConnectionStatus connection) {
      interactor.deviceStatePipe().send(DeviceStateCommand.connection(connection));
      observeActiveSmartCard(connection);
   }

   private void cardDisconnected() {
      interactor.deviceStatePipe().send(DeviceStateCommand.connection(DISCONNECTED));
      interactor.deviceStatePipe().send(DeviceStateCommand.battery(0));
      interactor.deviceStatePipe().send(DeviceStateCommand.lock(true));
   }

   private void observeActiveSmartCard(ConnectionStatus connectionStatus) {
      subscriptions.add(
            interactor.activeSmartCardPipe()
                  .observeSuccessWithReplay()
                  .map(Command::getResult)
                  .filter(smartCard -> connectionStatus == ConnectionStatus.CONNECTED
                        && smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE)
                  .takeUntil(interactor.disconnectPipe().observeSuccess())
                  .take(1)
                  .subscribe(aVoid -> activeCardConnected(),
                        throwable -> Timber.e(throwable, "Error while observe connection for active card"))
      );
   }

   private void activeCardConnected() {
      interactor.fetchCardPropertiesPipe().send(new FetchCardPropertiesCommand());
      interactor.cardsListPipe().send(CardListCommand.fetch());
      setupBatteryObserver();
      setupChargerEventObserver();
   }

   private void connectUpdateSmartCard() {
      subscriptions.add(interactor.fetchBatteryLevelPipe()
            .observeSuccess()
            .subscribe(command ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.battery(command.getResult()))
            ));

      subscriptions.add(interactor.stealthModePipe()
            .observeSuccess()
            .subscribe(command ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.stealthMode(command.getResult()))
            ));

      subscriptions.add(interactor.disableDefaultCardDelayPipe()
            .observeSuccess()
            .subscribe(command ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.disableCardDelay(command.getResult()))
            ));

      subscriptions.add(interactor.autoClearDelayPipe()
            .observeSuccess()
            .subscribe(command ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.clearFlyeDelay(command.getResult()))
            ));

      subscriptions.add(interactor.fetchFirmwareVersionPipe()
            .observeSuccess()
            .subscribe(command ->
                  interactor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.save(command.getResult()))
            ));

      subscriptions.add(Observable.merge(
            interactor.lockDeviceChangedEventPipe()
                  .observeSuccess()
                  .map(event -> event.locked),
            interactor.lockPipe().observeSuccess()
                  .map(SetLockStateCommand::isLock)
            )
                  .subscribe(
                        state -> interactor.deviceStatePipe().send(DeviceStateCommand.lock(state)),
                        throwable -> Timber.d(throwable, ""))
      );
   }

   private void setupBatteryObserver() {
      Observable.interval(0, 1, TimeUnit.MINUTES)
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .filter(inter -> !syncDisabled)
            .doOnNext(aLong -> Timber.d("setupBatteryObserver = %s", aLong))
            .subscribe(value ->
                        interactor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand()),
                  throwable -> {
                  });
   }

   private void setupChargerEventObserver() {
      interactor.cardInChargerEventPipe()
            .observeSuccess()
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .filter(cardInChargerEvent -> cardInChargerEvent.inCharger)
            .flatMap(smartCard ->
                  interactor.fetchFirmwareVersionPipe().createObservable(new FetchFirmwareVersionCommand()))
            .subscribe();
   }

   private void connectSyncCards() {
      subscriptions.add(Observable.interval(10, TimeUnit.MINUTES)
            .mergeWith(interactor.cardsListPipe().observeSuccess()
                  .map(cardListCommand -> null))
            .compose(new FilterActiveConnectedSmartCard(interactor))
            .filter(smartCard -> !syncDisabled)
            .throttleFirst(10, TimeUnit.MINUTES)
            .flatMap(aLong -> interactor.cardSyncPipe()
                  .createObservableResult(new SyncCardsCommand()))
            .retry(1)
            .subscribe(command -> {
            }, Throwable::printStackTrace));

      subscriptions.add(interactor.deleteCardPipe()
            .observeSuccess()
            .subscribe(deleteRecordAction ->
                  interactor.cardsListPipe()
                        .send(remove(valueOf(deleteRecordAction.recordId)))));

      subscriptions.add(interactor.addRecordPipe()
            .observeSuccess()
            .subscribe(attachCardCommand ->
                  interactor.cardsListPipe()
                        .send(add(attachCardCommand.getResult()))));

      subscriptions.add(interactor.updateBankCardPipe()
            .observeSuccess()
            .subscribe(updateBankCardCommand -> interactor.cardsListPipe()
                  .send(edit(updateBankCardCommand.getResult()))));

      //update cache default card
      subscriptions.add(interactor.setDefaultCardOnDeviceCommandPipe()
            .observeSuccess()
            .map(Command::getResult)
            .subscribe(id -> interactor.defaultCardIdPipe().send(DefaultCardIdCommand.set(id)), throwable -> {
            }));
   }

   private static final class FilterActiveConnectedSmartCard implements Observable.Transformer<Object, SmartCard> {

      private final SmartCardInteractor interactor;


      private FilterActiveConnectedSmartCard(SmartCardInteractor interactor) {this.interactor = interactor;}

      @Override
      public Observable<SmartCard> call(Observable<Object> target) {
         return target.flatMap(value ->
               Observable.zip(
                     interactor.activeSmartCardPipe()
                           .createObservableResult(new ActiveSmartCardCommand()),
                     interactor.deviceStatePipe()
                           .createObservableResult(DeviceStateCommand.fetch()),
                     (activeCommand, cardStateCommand) -> Pair.create(activeCommand.getResult(), cardStateCommand.getResult()))
                     .filter(pair -> pair.second.connectionStatus() == CONNECTED
                           && pair.first.cardStatus() == SmartCard.CardStatus.ACTIVE)
                     .map(pair -> pair.first));
      }
   }

}
