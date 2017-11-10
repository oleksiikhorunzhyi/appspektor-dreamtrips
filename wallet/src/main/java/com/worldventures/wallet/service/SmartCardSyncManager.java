package com.worldventures.wallet.service;

import android.support.v4.util.Pair;

import com.worldventures.wallet.domain.WalletConstants;
import com.worldventures.wallet.domain.entity.AboutSmartCardData;
import com.worldventures.wallet.domain.entity.CardStatus;
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.service.command.AboutSmartCardDataCommand;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.wallet.service.command.RecordListCommand;
import com.worldventures.wallet.service.command.SetLockStateCommand;
import com.worldventures.wallet.service.command.SetSmartCardTimeCommand;
import com.worldventures.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.wallet.service.command.record.SyncRecordStatusCommand;
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.wallet.service.firmware.command.LoadFirmwareFilesCommand;
import com.worldventures.wallet.util.WalletFeatureHelper;

import java.util.concurrent.TimeUnit;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.event.PinStatusEvent;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.wallet.domain.entity.ConnectionStatus.CONNECTED;
import static com.worldventures.wallet.domain.entity.ConnectionStatus.DFU;
import static com.worldventures.wallet.domain.entity.ConnectionStatus.DISCONNECTED;

public class SmartCardSyncManager {

   private final Janet janet;
   private final SmartCardInteractor interactor;
   private final RecordInteractor recordInteractor;
   private final WalletFeatureHelper featureHelper;
   private final FirmwareInteractor firmwareInteractor;

   private volatile boolean syncDisabled = false;

   public SmartCardSyncManager(Janet janet, SmartCardInteractor smartCardInteractor,
         FirmwareInteractor firmwareInteractor, RecordInteractor recordInteractor,
         WalletFeatureHelper featureHelper) {
      this.janet = janet;
      this.interactor = smartCardInteractor;
      this.firmwareInteractor = firmwareInteractor;
      this.recordInteractor = recordInteractor;
      this.featureHelper = featureHelper;
      observeConnection();
      connectUpdateSmartCard();
      connectSyncSmartCard();
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
      interactor.connectionActionPipe()
            .observeSuccess()
            .throttleLast(1, TimeUnit.SECONDS)
            .subscribe(connectAction -> cardConnected(connectAction.type == ConnectionType.DFU ? DFU : CONNECTED),
                  throwable -> Timber.e(throwable, "Error with handling connection event"));

      interactor.disconnectPipe()
            .observeSuccess()
            .subscribe(command -> cardDisconnected(), throwable -> Timber.e(throwable, "Error while updating status of active card"));
   }

   private void cardConnected(ConnectionStatus connection) {
      interactor.deviceStatePipe().send(DeviceStateCommand.Companion.connection(connection));
      observeActiveSmartCard(connection);
   }

   private void cardDisconnected() {
      interactor.deviceStatePipe().send(DeviceStateCommand.Companion.connection(DISCONNECTED));
      interactor.deviceStatePipe().send(DeviceStateCommand.Companion.battery(0));
   }

   private void observeActiveSmartCard(ConnectionStatus connectionStatus) {
      interactor.activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .filter(smartCard -> connectionStatus == CONNECTED
                  && smartCard.getCardStatus() == CardStatus.ACTIVE)
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .take(1)
            .subscribe(aVoid -> activeCardConnected(),
                  throwable -> Timber.e(throwable, "Error while observe connection for active card")
            );
   }

   private void activeCardConnected() {
      interactor.setSmartCardTimePipe().send(new SetSmartCardTimeCommand());
      interactor.fetchCardPropertiesPipe().send(new FetchCardPropertiesCommand());
      interactor.checkPinStatusActionPipe().send(new CheckPinStatusAction());
      interactor.getDisplayTypePipe().send(new GetDisplayTypeCommand(true));
      recordInteractor.cardsListPipe().send(RecordListCommand.Companion.fetch());
      setupBatteryObserver();
      setupChargerEventObserver();
   }

   private void connectUpdateSmartCard() {
      interactor.fetchBatteryLevelPipe()
            .observeSuccess()
            .subscribe(command ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.Companion.battery(command.getResult()))
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
                  interactor.deviceStatePipe().send(DeviceStateCommand.Companion.stealthMode(stealthModeEnabled))
            );

      Observable.merge(
            interactor.disableDefaultCardDelayPipe()
                  .observeSuccess()
                  .map(Command::getResult),
            interactor.getDisableDefaultCardDelayPipe()
                  .observeSuccess()
                  .map(action -> action.delay))
            .subscribe(disableCardDelay ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.Companion.disableCardDelay(disableCardDelay))
            );

      Observable.merge(
            interactor.autoClearDelayPipe()
                  .observeSuccess()
                  .map(Command::getResult),
            interactor.getAutoClearDelayPipe()
                  .observeSuccess()
                  .map(action -> action.delay))
            .subscribe(clearDelay ->
                  interactor.deviceStatePipe().send(DeviceStateCommand.Companion.clearFlyeDelay(clearDelay))
            );

      interactor.fetchFirmwareVersionPipe()
            .observeSuccess()
            .map(Command::getResult)
            .distinctUntilChanged()
            .subscribe(firmware -> {
                     saveFirmwareDataForAboutScreen(firmware);
                     interactor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.Companion.save(firmware));
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
                  .map(SetLockStateCommand::isLock),
            interactor.pinStatusEventPipe()
                  .observeSuccess()
                  .map(pinStatusEvent -> pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.AUTHENTICATED
                        && pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.DISABLED)
      )
            .subscribe(
                  state -> interactor.deviceStatePipe().send(DeviceStateCommand.Companion.lock(state)),
                  throwable -> Timber.d(throwable, "")
            );
   }

   private void saveFirmwareDataForAboutScreen(SmartCardFirmware firmware) {
      if (!firmware.isEmpty()) {
         interactor.aboutSmartCardDataCommandPipe()
               .send(AboutSmartCardDataCommand.save(new AboutSmartCardData(firmware)));
      }
   }

   private void setupBatteryObserver() {
      Observable.interval(0, 1, TimeUnit.MINUTES)
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .filter(inter -> !syncDisabled)
            .doOnNext(aLong -> Timber.d("setupBatteryObserver = %s", aLong))
            .subscribe(value ->
                        interactor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand()),
                  throwable -> Timber.e(throwable, ""));
   }

   private void setupChargerEventObserver() {
      interactor.cardInChargerEventPipe()
            .observeSuccess()
            .takeUntil(interactor.disconnectPipe().observeSuccess())
            .filter(cardInChargerEvent -> cardInChargerEvent.inCharger)
            .subscribe(cardInChargerEvent ->
                  interactor.fetchFirmwareVersionPipe().send(new FetchFirmwareVersionCommand()));
   }

   private void connectSyncSmartCard() {
      if (featureHelper.isSampleCardMode()) {
         return;
      }
      Observable.interval(WalletConstants.AUTO_SYNC_PERIOD_MINUTES, TimeUnit.MINUTES)
            .mergeWith(recordInteractor.cardsListPipe().observeSuccess()
                  .map(cardListCommand -> null))
            .compose(new FilterActiveConnectedSmartCard(interactor))
            .filter(smartCard -> !syncDisabled)
            .throttleFirst(WalletConstants.AUTO_SYNC_PERIOD_MINUTES, TimeUnit.MINUTES)
            .flatMap(aLong -> recordInteractor.syncRecordStatusPipe()
                  .createObservableResult(SyncRecordStatusCommand.fetch()))
            .map(Command::getResult)
            .filter(status -> !status.isFailAfterProvision())
            .flatMap(aLong -> interactor.smartCardSyncPipe()
                  .createObservableResult(new SyncSmartCardCommand()))
            .retry(1)
            .subscribe(command -> { /*nothing*/ }, throwable -> Timber.e(throwable, ""));
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
                           .createObservableResult(DeviceStateCommand.Companion.fetch()),
                     (activeCommand, cardStateCommand) -> Pair.create(activeCommand.getResult(), cardStateCommand.getResult()))
                     .filter(pair -> pair.second.getConnectionStatus() == CONNECTED
                           && pair.first.getCardStatus() == CardStatus.ACTIVE)
                     .map(pair -> pair.first));
      }
   }

}
