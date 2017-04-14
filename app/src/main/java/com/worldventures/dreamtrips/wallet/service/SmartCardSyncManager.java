package com.worldventures.dreamtrips.wallet.service;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetPinEnabledCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.LoadFirmwareFilesCommand;

import java.util.concurrent.TimeUnit;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.DFU;
import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.DISCONNECTED;

public class SmartCardSyncManager {

   private final Janet janet;
   private final SmartCardInteractor interactor;
   private final RecordInteractor recordInteractor;
   private final FirmwareInteractor firmwareInteractor;

   private volatile boolean syncDisabled = false;

   public SmartCardSyncManager(Janet janet, SmartCardInteractor smartCardInteractor,
         FirmwareInteractor firmwareInteractor, RecordInteractor recordInteractor) {
      this.janet = janet;
      this.interactor = smartCardInteractor;
      this.firmwareInteractor = firmwareInteractor;
      this.recordInteractor = recordInteractor;
      observeConnection();
      connectUpdateSmartCard();
      connectSyncCards();
      connectSyncDisabling();
   }

   private void connectSyncDisabling() {
      janet.createPipe(LoadFirmwareFilesCommand.class)
            .observeWithReplay()
            .subscribe(new ActionStateSubscriber<LoadFirmwareFilesCommand>()
                  .onStart(loadFirmwareFilesCommand -> syncDisabled = true)
                  .onFinish(loadFirmwareFilesCommand -> syncDisabled = false));
   }

   private void observeConnection() {
      // // TODO: 2/20/17 create pipe in interactor
      janet.createPipe(ConnectAction.class)
            .observeSuccess()
            .throttleLast(1, TimeUnit.SECONDS)
            .subscribe(connectAction -> cardConnected(connectAction.type == ConnectionType.DFU ? DFU : CONNECTED),
                  throwable -> Timber.e(throwable, "Error with handling connection event"));

      interactor.disconnectPipe()
            .observeSuccess()
            .subscribe(command -> cardDisconnected(), throwable -> Timber.e(throwable, "Error while updating status of active card"));
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
      interactor.activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .filter(smartCard -> connectionStatus == ConnectionStatus.CONNECTED
                  && smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE)
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .take(1)
            .subscribe(aVoid -> activeCardConnected(),
                  throwable -> Timber.e(throwable, "Error while observe connection for active card")
            );
   }

   private void activeCardConnected() {
      interactor.fetchCardPropertiesPipe().send(new FetchCardPropertiesCommand());
      interactor.getPinEnabledCommandActionPipe().send(new GetPinEnabledCommand());
      recordInteractor.cardsListPipe().send(RecordListCommand.fetch());
      setupBatteryObserver();
      setupChargerEventObserver();
   }

   private void connectUpdateSmartCard() {
      interactor.fetchBatteryLevelPipe()
            .observeSuccess()
            .subscribe(command ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.battery(command.getResult()))
            );

      Observable.merge(
            interactor.getStealthModePipe()
                  .observeSuccess()
                  .map(action -> action.enabled),
            interactor.stealthModePipe()
                  .observeSuccess()
                  .map(Command::getResult)
      )
            .subscribe(stealthModeEnabled ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.stealthMode(stealthModeEnabled))
            );

      Observable.merge(
            interactor.disableDefaultCardDelayPipe()
                  .observeSuccess()
                  .map(Command::getResult),
            interactor.getDisableDefaultCardDelayPipe()
                  .observeSuccess()
                  .map(action -> action.delay))
            .subscribe(disableCardDelay ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.disableCardDelay(disableCardDelay))
            );

      Observable.merge(
            interactor.autoClearDelayPipe()
                  .observeSuccess()
                  .map(Command::getResult),
            interactor.getAutoClearDelayPipe()
                  .observeSuccess()
                  .map(action -> action.delay))
            .subscribe(clearDelay ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.clearFlyeDelay(clearDelay))
            );

      interactor.fetchFirmwareVersionPipe()
            .observeSuccess()
            .map(Command::getResult)
            .subscribe(firmware -> {
                     interactor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.save(firmware));
                     firmwareInteractor.fetchFirmwareInfoPipe().send(new FetchFirmwareInfoCommand(firmware));
                  }
            );

      Observable.merge(
            interactor.getLockPipe()
                  .observeSuccess()
                  .map(action -> action.locked),
            interactor.lockDeviceChangedEventPipe()
                  .observeSuccess()
                  .map(event -> event.locked),
            interactor.lockPipe().observeSuccess()
                  .map(SetLockStateCommand::isLock)
      )
            .subscribe(
                  state -> interactor.deviceStatePipe().send(DeviceStateCommand.lock(state)),
                  throwable -> Timber.d(throwable, "")
            );
   }

   private void setupBatteryObserver() {
      Observable.interval(0, 1, TimeUnit.MINUTES)
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .filter(inter -> !syncDisabled)
            .doOnNext(aLong -> Timber.d("setupBatteryObserver = %s", aLong))
            .subscribe(value ->
                        interactor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand()),
                  throwable -> Timber.e("", throwable));
   }

   private void setupChargerEventObserver() {
      interactor.cardInChargerEventPipe()
            .observeSuccess()
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .filter(cardInChargerEvent -> cardInChargerEvent.inCharger)
            .subscribe(cardInChargerEvent ->
                  interactor.fetchFirmwareVersionPipe().send(new FetchFirmwareVersionCommand()));
   }

   private void connectSyncCards() {
      Observable.interval(10, TimeUnit.MINUTES)
            .mergeWith(recordInteractor.cardsListPipe().observeSuccess()
                  .map(cardListCommand -> null))
            .compose(new FilterActiveConnectedSmartCard(interactor))
            .filter(smartCard -> !syncDisabled)
            .throttleFirst(10, TimeUnit.MINUTES)
            .flatMap(aLong -> recordInteractor.syncRecordStatusPipe()
                  .createObservableResult(SyncRecordStatusCommand.fetch()))
            .map(Command::getResult)
            .filter(status -> !status.isFailAfterProvision())
            .flatMap(aLong -> recordInteractor.recordsSyncPipe()
                  .createObservableResult(new SyncRecordsCommand()))
            .retry(1)
            .subscribe(command -> {
            }, throwable -> Timber.e("", throwable));
   }

   private static final class FilterActiveConnectedSmartCard implements Observable.Transformer<Object, SmartCard> {

      private final SmartCardInteractor interactor;

      private FilterActiveConnectedSmartCard(SmartCardInteractor interactor) {
         this.interactor = interactor;
      }

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
