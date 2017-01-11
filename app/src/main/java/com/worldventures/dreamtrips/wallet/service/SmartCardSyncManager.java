package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.service.command.firmware.LoadFirmwareFilesCommand;

import java.util.concurrent.TimeUnit;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DFU;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DISCONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.ERROR;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.add;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.edit;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.remove;
import static java.lang.String.valueOf;

class SmartCardSyncManager {

   private final Janet janet;
   private final SmartCardInteractor interactor;
   private volatile boolean syncDisabled = true;

   SmartCardSyncManager(Janet janet, SmartCardInteractor interactor) {
      this.janet = janet;
      this.interactor = interactor;
      observeConnection();
      connectUpdateSmartCard();
      connectFetchingBattery();
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
      janet.createPipe(ConnectAction.class)
            .observeSuccess()
            .map(connectAction -> connectAction.type)
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(this::smartCardConnected, throwable -> Timber.e(throwable, "Error with handling connection event"));

      interactor.disconnectPipe()
            .observe()
            .filter(state -> state.status == ActionState.Status.SUCCESS || state.status == ActionState.Status.FAIL)
            .map(state -> state.status == ActionState.Status.SUCCESS ? DISCONNECTED : ERROR)
            .flatMap(connectionStatus -> interactor.activeSmartCardPipe()
                  .createObservableResult(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.copyOf(smartCard)
                              .withConnectionStatus(connectionStatus))))
            .subscribe(command -> {
            }, throwable -> Timber.e(throwable, "Error while updating status of active card"));
   }

   private void smartCardConnected(ConnectionType connectionType) {
      SmartCard.ConnectionStatus status = CONNECTED;
      if (connectionType == ConnectionType.DFU) {
         status = DFU;
      }

      SmartCard.ConnectionStatus finalStatus = status;
      interactor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand(smartCard ->
                  ImmutableSmartCard.copyOf(smartCard)
                        .withConnectionStatus(finalStatus)))
            .map(ActiveSmartCardCommand::getResult)
            .filter(smartCard -> smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE
                  && smartCard.connectionStatus() == CONNECTED)
            .subscribe(smartCard -> {
               interactor.fetchCardPropertiesPipe()
                     .send(new FetchCardPropertiesCommand());
               interactor.cardsListPipe().send(CardListCommand.fetch());
            }, throwable -> {
            });
   }

   private void connectUpdateSmartCard() {
      interactor.fetchBatteryLevelPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(level -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard -> ImmutableSmartCard.builder()
                        .from(smartCard)
                        .batteryLevel(Integer.parseInt(level))
                        .build())), throwable -> {
            });

      interactor.connectActionPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(smartCard -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard)), throwable -> {
            });

      interactor.stealthModePipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(value -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.builder()
                              .from(smartCard)
                              .stealthMode(value)
                              .build())), throwable -> {
            });

      interactor.disableDefaultCardDelayPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(delay -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.builder()
                              .from(smartCard)
                              .disableCardDelay(delay)
                              .build())), throwable -> {
            });

      interactor.autoClearDelayPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(delay -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.builder()
                              .from(smartCard)
                              .clearFlyeDelay(delay)
                              .build())), throwable -> {
            });

      interactor.fetchCardPropertiesPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(properties -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard -> {
                     ImmutableSmartCardFirmware smartCardFirmware = ImmutableSmartCardFirmware.copyOf(properties.firmwareVersion());
                     if (smartCard.firmwareVersion() != null) {
                        smartCardFirmware = smartCardFirmware
                              .withFirmwareVersion(smartCard.firmwareVersion().firmwareVersion());
                     }
                     return ImmutableSmartCard.builder()
                           .from(smartCard)
                           .sdkVersion(properties.sdkVersion())
                           .firmwareVersion(smartCardFirmware)
                           .batteryLevel(properties.batteryLevel())
                           .lock(properties.lock())
                           .stealthMode(properties.stealthMode())
                           .disableCardDelay(properties.disableCardDelay())
                           .clearFlyeDelay(properties.clearFlyeDelay())
                           .build();
                  })), throwable -> {
            });

      Observable.merge(
            interactor.lockDeviceChangedEventPipe()
                  .observeSuccess()
                  .map(event -> event.locked),
            interactor.lockPipe().observeSuccess()
                  .map(SetLockStateCommand::isLock)
      ).subscribe(state -> interactor.activeSmartCardPipe()
            .send(new ActiveSmartCardCommand(smartCard ->
                  ImmutableSmartCard.builder()
                        .from(smartCard)
                        .lock(state)
                        .build())), throwable -> {
      });
   }

   private void connectFetchingBattery() {
      Observable.interval(0, 1, TimeUnit.MINUTES)
            .flatMap(aLong -> interactor.activeSmartCardPipe()
                  .createObservableResult(new ActiveSmartCardCommand()))
            .map(Command::getResult)
            .filter(smartCard -> smartCard.connectionStatus() == CONNECTED
                  && smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE)
            .filter(smartCard -> !syncDisabled)
            .subscribe(value ->
                        interactor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand()),
                  throwable -> {
                  });
   }

   private void connectSyncCards() {
      Observable.interval(10, TimeUnit.MINUTES)
            .mergeWith(interactor.cardsListPipe().observeSuccess()
                  .map(cardListCommand -> null))
            .flatMap(aLong -> interactor.activeSmartCardPipe()
                  .createObservableResult(new ActiveSmartCardCommand()))
            .map(Command::getResult)
            .filter(smartCard -> smartCard.connectionStatus() == CONNECTED
                  && smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE)
            .filter(smartCard -> !syncDisabled)
            .throttleFirst(10, TimeUnit.MINUTES)
            .flatMap(aLong -> interactor.cardSyncPipe()
                  .createObservableResult(new SyncCardsCommand()))
            .retry(1)
            .subscribe(command -> {
            }, Throwable::printStackTrace);

      interactor.deleteCardPipe()
            .observeSuccess()
            .subscribe(deleteRecordAction ->
                  interactor.cardsListPipe()
                        .send(remove(valueOf(deleteRecordAction.recordId))));

      interactor.addRecordPipe()
            .observeSuccess()
            .subscribe(attachCardCommand ->
                  interactor.cardsListPipe()
                        .send(add(attachCardCommand.getResult())));

      interactor.updateBankCardPipe()
            .observeSuccess()
            .map(Command::getResult)
            .mergeWith(interactor.updateCardDetailsPipe()
                  .observeSuccess()
                  .map(Command::getResult))
            .subscribe(result -> interactor.cardsListPipe()
                  .send(edit(result)));

      //update cache default card
      interactor.setDefaultCardOnDeviceCommandPipe()
            .observeSuccess()
            .map(Command::getResult)
            .subscribe(id -> interactor.defaultCardIdPipe().send(DefaultCardIdCommand.set(id)), throwable -> {
            });
   }

}
