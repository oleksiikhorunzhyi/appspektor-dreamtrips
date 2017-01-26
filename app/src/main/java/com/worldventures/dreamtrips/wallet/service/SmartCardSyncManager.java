package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.LoadFirmwareFilesCommand;

import java.util.concurrent.TimeUnit;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DFU;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DISCONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.ERROR;
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
         connectFetchingBattery();
         connectSyncCards();
         connectSyncDisabling();
         connectFetchingFirmwareVersion();
      }
   }

   public void disconnect() {
      if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
         subscriptions.clear();
      }
   }

   private void connectFetchingFirmwareVersion() {
      subscriptions.add(
            interactor.cardInChargerEventPipe()
                  .observeSuccess()
                  .filter(cardInChargerEvent -> cardInChargerEvent.inCharger)
                  .compose(new FilterActiveConnectedSmartCard(interactor))
                  .flatMap(smartCard ->
                        interactor.fetchFirmwareVersionPipe()
                              .createObservable(new FetchFirmwareVersionCommand())
                              .filter(actionState -> actionState.status == ActionState.Status.SUCCESS)
                              .map(actionState -> actionState.action.getResult()))
                  .flatMap(firmwareVersion ->
                        interactor.activeSmartCardPipe()
                              .createObservable(new ActiveSmartCardCommand(smartCard ->
                                    ImmutableSmartCard.copyOf(smartCard)
                                          .withFirmwareVersion(firmwareVersion))))
                  .subscribe()
      );
   }

   private void connectSyncDisabling() {
      subscriptions.add(janet.createPipe(LoadFirmwareFilesCommand.class)
            .observeWithReplay()
            .subscribe(new ActionStateSubscriber<LoadFirmwareFilesCommand>()
                  .onStart(loadFirmwareFilesCommand -> syncDisabled = true)
                  .onFinish(loadFirmwareFilesCommand -> syncDisabled = false)));
   }

   private void observeConnection() {
      subscriptions.add(janet.createPipe(ConnectAction.class)
            .observeSuccess()
            .map(connectAction -> connectAction.type)
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(this::smartCardConnected, throwable -> Timber.e(throwable, "Error with handling connection event")));

      subscriptions.add(interactor.disconnectPipe()
            .observe()
            .filter(state -> state.status == ActionState.Status.SUCCESS || state.status == ActionState.Status.FAIL)
            .map(state -> state.status == ActionState.Status.SUCCESS ? DISCONNECTED : ERROR)
            .flatMap(connectionStatus -> interactor.activeSmartCardPipe()
                  .createObservableResult(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.copyOf(smartCard)
                              .withConnectionStatus(connectionStatus)))
                  .filter(command -> command.getCacheData() != null))
            .subscribe(command -> {
            }, throwable -> Timber.e(throwable, "Error while updating status of active card")));
   }

   private void smartCardConnected(ConnectionType connectionType) {
      SmartCard.ConnectionStatus status = CONNECTED;
      if (connectionType == ConnectionType.DFU) {
         status = DFU;
      }

      SmartCard.ConnectionStatus finalStatus = status;
      subscriptions.add(interactor.activeSmartCardPipe()
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
            }));
   }

   private void connectUpdateSmartCard() {
      subscriptions.add(interactor.fetchBatteryLevelPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(level -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard -> ImmutableSmartCard.builder()
                        .from(smartCard)
                        .batteryLevel(Integer.parseInt(level))
                        .build())), throwable -> {
            }));

      subscriptions.add(interactor.connectActionPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(smartCard -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard)), throwable -> {
            }));

      subscriptions.add(interactor.stealthModePipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(value -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.builder()
                              .from(smartCard)
                              .stealthMode(value)
                              .build())), throwable -> {
            }));

      subscriptions.add(interactor.disableDefaultCardDelayPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(delay -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.builder()
                              .from(smartCard)
                              .disableCardDelay(delay)
                              .build())), throwable -> {
            }));

      subscriptions.add(interactor.autoClearDelayPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(delay -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard ->
                        ImmutableSmartCard.builder()
                              .from(smartCard)
                              .clearFlyeDelay(delay)
                              .build())), throwable -> {
            }));

      subscriptions.add(interactor.fetchCardPropertiesPipe().observeSuccess()
            .map(Command::getResult)
            .subscribe(properties -> interactor.activeSmartCardPipe()
                  .send(new ActiveSmartCardCommand(smartCard -> ImmutableSmartCard.builder()
                        .from(smartCard)
                        .sdkVersion(properties.sdkVersion())
                        .firmwareVersion(updateSmartCardFirmware(smartCard, properties.firmwareVersion()))
                        .batteryLevel(properties.batteryLevel())
                        .lock(properties.lock())
                        .stealthMode(properties.stealthMode())
                        .disableCardDelay(properties.disableCardDelay())
                        .clearFlyeDelay(properties.clearFlyeDelay())
                        .build()
                  )), throwable -> {
            }));

      subscriptions.add(Observable.merge(
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
      }));
   }

   private SmartCardFirmware updateSmartCardFirmware(SmartCard smartCard, SmartCardFirmware firmware) {
      final SmartCardFirmware scFirmware = smartCard.firmwareVersion();
      if (scFirmware == null) return firmware;

      return ImmutableSmartCardFirmware
            .builder()
            .from(firmware)
            .firmwareBundleVersion(scFirmware.firmwareBundleVersion())
            .build();
   }

   private void connectFetchingBattery() {
      subscriptions.add(Observable.interval(0, 1, TimeUnit.MINUTES)
            .compose(new FilterActiveConnectedSmartCard(interactor))
            .filter(smartCard -> !syncDisabled)
            .subscribe(value ->
                        interactor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand()),
                  throwable -> {
                  }));
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
            .observe()
            .subscribe(new ActionStateSubscriber<UpdateBankCardCommand>()
                  .onStart(updateBankCardCommand -> interactor.cardsListPipe()
                        .send(edit(updateBankCardCommand.getBankCard())))
                  .onSuccess(updateBankCardCommand -> interactor.cardsListPipe()
                        .send(edit(updateBankCardCommand.getResult())))));

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
         return target.flatMap(value -> interactor.activeSmartCardPipe()
               .createObservableResult(new ActiveSmartCardCommand()))
               .map(Command::getResult)
               .filter(smartCard -> smartCard.connectionStatus() == CONNECTED
                     && smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE);
      }
   }

}
