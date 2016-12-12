package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.AddBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardModifier;
import com.worldventures.dreamtrips.wallet.service.command.UpdateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateSmartCardConnectionStatus;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAssociatedSmartCardCommand;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import io.techery.janet.smartcard.action.user.GetUserDataAction;
import io.techery.janet.smartcard.action.user.UnAssignUserAction;
import io.techery.janet.smartcard.event.CardChargedEvent;
import io.techery.janet.smartcard.event.CardSwipedEvent;
import io.techery.janet.smartcard.event.LockDeviceChangedEvent;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DFU;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DISCONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.ERROR;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.add;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.edit;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.remove;
import static java.lang.String.valueOf;

public final class SmartCardInteractor {
   private final ActionPipe<ConnectSmartCardCommand> connectionPipe;
   private final ActionPipe<CardListCommand> cardsListPipe;
   private final WriteActionPipe<CardListCommand> cardsListInnerPipe; // hotfix, see constructor
   private final ActionPipe<AttachCardCommand> addRecordPipe;
   private final ActionPipe<UpdateBankCardCommand> updateBankCardPipe;
   private final ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCardPipe;
   private final ActionPipe<CardStacksCommand> cardStacksPipe;
   private final ActionPipe<GetActiveSmartCardCommand> activeSmartCardPipe;
   private final ActionPipe<GetDefaultAddressCommand> getDefaultAddressCommandPipe;
   private final ActionPipe<AddBankCardCommand> saveCardDetailsDataCommandPipe;
   private final ActionPipe<SetStealthModeCommand> stealthModePipe;
   private final ActionPipe<SetLockStateCommand> setLockPipe;
   private final ActionPipe<SaveLockStateCommand> saveLockStatePipe;
   private final ReadActionPipe<LockDeviceChangedEvent> lockDeviceChangedEventPipe;
   private final ReadActionPipe<SmartCardModifier> smartCardModifierPipe;
   private final ActionPipe<FetchDefaultCardIdCommand> fetchDefaultCardIdCommandPipe;
   private final ActionPipe<FetchDefaultCardCommand> fetchDefaultCardCommandPipe;
   private final WriteActionPipe<FetchBatteryLevelCommand> fetchBatteryLevelPipe;
   private final ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe;
   private final ActionPipe<SetPaymentCardAction> setPaymentCardActionActionPipe;
   private final ActionPipe<DeleteRecordAction> deleteCardPipe;
   private final ActionPipe<UpdateCardDetailsDataCommand> updateCardDetailsPipe;
   private final ActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<UpdateSmartCardConnectionStatus> updateSmartCardConnectionStatusPipe;
   private final ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe;

   private final ReadActionPipe<CardChargedEvent> chargedEventPipe;
   private final ReadActionPipe<CardSwipedEvent> cardSwipedEventPipe;
   private final ActionPipe<StartCardRecordingAction> startCardRecordingPipe;
   private final ActionPipe<StopCardRecordingAction> stopCardRecordingPipe;
   private final ActionPipe<CreateBankCardCommand> recordIssuerInfoPipe;

   private final ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe;
   private final ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardPipe;

   private final ActionPipe<GetCompatibleDevicesCommand> compatibleDevicesActionPipe;

   private final ActionPipe<GetUserDataAction> userDataActionActionPipe;

   private final FirmwareInteractor firmwareInteractor;

   public SmartCardInteractor(@Named(JANET_WALLET) Janet janet, SessionActionPipeCreator sessionActionPipeCreator, FirmwareInteractor firmwareInteractor) {
      connectionPipe = sessionActionPipeCreator.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      cardsListPipe = sessionActionPipeCreator.createPipe(CardListCommand.class, Schedulers.from(Executors.newSingleThreadExecutor()));
      cardsListInnerPipe = janet.createPipe(CardListCommand.class); //todo: hotfix: code in `observeCardsChanges` should be synchronous
      addRecordPipe = sessionActionPipeCreator.createPipe(AttachCardCommand.class, Schedulers.io());
      updateBankCardPipe = sessionActionPipeCreator.createPipe(UpdateBankCardCommand.class, Schedulers.io());
      fetchAssociatedSmartCardPipe = sessionActionPipeCreator.createPipe(FetchAssociatedSmartCardCommand.class, Schedulers
            .io());
      cardStacksPipe = sessionActionPipeCreator.createPipe(CardStacksCommand.class, Schedulers.io());
      activeSmartCardPipe = sessionActionPipeCreator.createPipe(GetActiveSmartCardCommand.class, Schedulers.io());
      stealthModePipe = sessionActionPipeCreator.createPipe(SetStealthModeCommand.class, Schedulers.io());

      smartCardModifierPipe = sessionActionPipeCreator.createPipe(SmartCardModifier.class, Schedulers.io());
      lockDeviceChangedEventPipe = sessionActionPipeCreator.createPipe(LockDeviceChangedEvent.class, Schedulers.io());
      setLockPipe = sessionActionPipeCreator.createPipe(SetLockStateCommand.class, Schedulers.io());
      saveLockStatePipe = sessionActionPipeCreator.createPipe(SaveLockStateCommand.class, Schedulers.io());

      getDefaultAddressCommandPipe = sessionActionPipeCreator.createPipe(GetDefaultAddressCommand.class, Schedulers.io());
      saveCardDetailsDataCommandPipe = sessionActionPipeCreator.createPipe(AddBankCardCommand.class, Schedulers.io());
      fetchDefaultCardIdCommandPipe = sessionActionPipeCreator.createPipe(FetchDefaultCardIdCommand.class, Schedulers.io());
      fetchDefaultCardCommandPipe = sessionActionPipeCreator.createPipe(FetchDefaultCardCommand.class, Schedulers.io());
      fetchBatteryLevelPipe = sessionActionPipeCreator.createPipe(FetchBatteryLevelCommand.class, Schedulers.io());
      setDefaultCardOnDeviceCommandPipe = sessionActionPipeCreator.createPipe(SetDefaultCardOnDeviceCommand.class, Schedulers
            .io());
      setPaymentCardActionActionPipe = sessionActionPipeCreator.createPipe(SetPaymentCardAction.class, Schedulers.io());
      deleteCardPipe = sessionActionPipeCreator.createPipe(DeleteRecordAction.class, Schedulers.io());
      updateCardDetailsPipe = sessionActionPipeCreator.createPipe(UpdateCardDetailsDataCommand.class, Schedulers.io());

      disconnectPipe = sessionActionPipeCreator.createPipe(DisconnectAction.class, Schedulers.io());
      updateSmartCardConnectionStatusPipe = sessionActionPipeCreator.createPipe(UpdateSmartCardConnectionStatus.class, Schedulers
            .io());
      restartSmartCardCommandActionPipe = sessionActionPipeCreator.createPipe(RestartSmartCardCommand.class, Schedulers.io());

      chargedEventPipe = sessionActionPipeCreator.createPipe(CardChargedEvent.class, Schedulers.io());
      cardSwipedEventPipe = sessionActionPipeCreator.createPipe(CardSwipedEvent.class, Schedulers.io());
      startCardRecordingPipe = sessionActionPipeCreator.createPipe(StartCardRecordingAction.class, Schedulers.io());
      stopCardRecordingPipe = sessionActionPipeCreator.createPipe(StopCardRecordingAction.class, Schedulers.io());

      recordIssuerInfoPipe = sessionActionPipeCreator.createPipe(CreateBankCardCommand.class, Schedulers.io());

      autoClearDelayPipe = sessionActionPipeCreator.createPipe(SetAutoClearSmartCardDelayCommand.class, Schedulers.io());
      disableDefaultCardPipe = sessionActionPipeCreator.createPipe(SetDisableDefaultCardDelayCommand.class, Schedulers.io());
      userDataActionActionPipe = sessionActionPipeCreator.createPipe(GetUserDataAction.class, Schedulers.io());

      this.firmwareInteractor = firmwareInteractor;

      compatibleDevicesActionPipe = sessionActionPipeCreator.createPipe(GetCompatibleDevicesCommand.class, Schedulers.io());

      connect(janet);
      connectToLockEvent();
      observeBatteryLevel(janet);
   }

   public ActionPipe<CardListCommand> cardsListPipe() {
      return cardsListPipe;
   }

   public ReadActionPipe<SmartCardModifier> smartCardModifierPipe() {
      return smartCardModifierPipe;
   }

   public ActionPipe<ConnectSmartCardCommand> connectActionPipe() {
      return connectionPipe;
   }

   public WriteActionPipe<AttachCardCommand> addRecordPipe() {
      return addRecordPipe;
   }

   public ActionPipe<UpdateBankCardCommand> updateBankCardPipe() {
      return updateBankCardPipe;
   }

   public ActionPipe<CardStacksCommand> cardStacksPipe() {
      return cardStacksPipe;
   }

   public ActionPipe<FetchDefaultCardIdCommand> fetchDefaultCardIdCommandPipe() {
      return fetchDefaultCardIdCommandPipe;
   }

   public ActionPipe<FetchDefaultCardCommand> fetchDefaultCardCommandPipe() {
      return fetchDefaultCardCommandPipe;
   }

   public ActionPipe<DeleteRecordAction> deleteCardPipe() {
      return deleteCardPipe;
   }

   public ActionPipe<UpdateCardDetailsDataCommand> updatePipe() {
      return updateCardDetailsPipe;
   }

   public ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCard() {
      return fetchAssociatedSmartCardPipe;
   }

   public ActionPipe<GetActiveSmartCardCommand> activeSmartCardPipe() {
      return activeSmartCardPipe;
   }

   public ActionPipe<GetDefaultAddressCommand> getDefaultAddressCommandPipe() {
      return getDefaultAddressCommandPipe;
   }

   public ActionPipe<SetStealthModeCommand> stealthModePipe() {
      return stealthModePipe;
   }

   public ActionPipe<SetLockStateCommand> lockPipe() {
      return setLockPipe;
   }

   public ReadActionPipe<LockDeviceChangedEvent> lockDeviceChangedEventPipe() {
      return lockDeviceChangedEventPipe;
   }

   public ActionPipe<AddBankCardCommand> saveCardDetailsDataPipe() {
      return saveCardDetailsDataCommandPipe;
   }

   public ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe() {
      return setDefaultCardOnDeviceCommandPipe;
   }

   public ActionPipe<SetPaymentCardAction> setPaymentCardActionActionPipe() {
      return setPaymentCardActionActionPipe;
   }

   public ReadActionPipe<CardChargedEvent> chargedEventPipe() {
      return chargedEventPipe;
   }

   public ReadActionPipe<CardSwipedEvent> cardSwipedEventPipe() {
      return cardSwipedEventPipe;
   }

   public ActionPipe<StartCardRecordingAction> startCardRecordingPipe() {
      return startCardRecordingPipe;
   }

   public ActionPipe<StopCardRecordingAction> stopCardRecordingPipe() {
      return stopCardRecordingPipe;
   }


   public ActionPipe<CreateBankCardCommand> bankCardPipe() {
      return recordIssuerInfoPipe;
   }

   public WriteActionPipe<DisconnectAction> disconnectPipe() {
      return disconnectPipe;
   }

   public ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe() {
      return restartSmartCardCommandActionPipe;
   }

   public ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe() {
      return autoClearDelayPipe;
   }

   public ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardPipe() {
      return disableDefaultCardPipe;
   }

   public ActionPipe<GetCompatibleDevicesCommand> compatibleDevicesActionPipe() {
      return compatibleDevicesActionPipe;
   }

   public ActionPipe<SaveLockStateCommand> lockStatePipe() {
      return saveLockStatePipe;
   }

   private void connect(Janet janet) {
      disconnectPipe
            .observe()
            .filter(state -> state.status == ActionState.Status.SUCCESS || state.status == ActionState.Status.FAIL)
            .map(state -> state.status == ActionState.Status.SUCCESS ? DISCONNECTED : ERROR)
            .subscribe(connectionStatus -> updateSmartCardConnectionStatusPipe.send(new UpdateSmartCardConnectionStatus(connectionStatus)),
                  throwable -> Timber.e(throwable, "Error while updating status of active card"));

      janet.createPipe(ConnectAction.class)
            .observeSuccess()
            .subscribe(action -> {
               SmartCard.ConnectionStatus status = CONNECTED;
               if (action.type == ConnectionType.DFU) {
                  status = DFU;
               }
               updateSmartCardConnectionStatusPipe.send(new UpdateSmartCardConnectionStatus(status));
            }, throwable -> Timber.e(throwable, "Error with handling connection event"));

      observeCardsChanges();
      connectFetchingCards();
   }

   private void connectFetchingCards() {
      smartCardModifierPipe.observeSuccess()
            .map(SmartCardModifier::getResult)
            .filter(smartCard -> smartCard.connectionStatus() == CONNECTED)
            .subscribe(smartCard -> cardStacksPipe.send(CardStacksCommand.get(false)));
   }

   //TODO this way of syncing data between pipes is quite unobvious and should be reworked in future
   private void observeCardsChanges() {
      Observable.merge(
            deleteCardPipe
                  .observeSuccess()
                  .flatMap(deleteCommand -> cardsListInnerPipe.createObservable(remove(valueOf(deleteCommand.recordId)))),
            addRecordPipe
                  .observeSuccess()
                  .flatMap(attachCardCommand -> cardsListInnerPipe.createObservable(add(attachCardCommand.getResult()))),
            updateBankCardPipe
                  .observeSuccess()
                  .flatMap(updateBankCardCommand -> cardsListInnerPipe.createObservable(edit(updateBankCardCommand.getResult()))),
            updateCardDetailsPipe
                  .observeSuccess()
                  .flatMap(editCardCommand -> cardsListInnerPipe.createObservable(edit(editCardCommand.getResult()))))
            .subscribe(new ActionStateSubscriber<CardListCommand>()
                  .onSuccess(cardListCommand -> cardStacksPipe.send(CardStacksCommand.get(false), Schedulers.immediate()))
                  .onFail((cardListCommand, throwable) -> {
                     throw new IllegalStateException("Cannot perform operation onto card list cache", throwable);
                  }));

   }

   private void connectToLockEvent() {
      Observable.merge(
            lockDeviceChangedEventPipe
                  .observeSuccess()
                  .map(event -> event.locked),
            setLockPipe.observeSuccess()
                  .map(SetLockStateCommand::isLock)
      ).subscribe(lock -> saveLockStatePipe.send(new SaveLockStateCommand(lock)),
            throwable -> Timber.e(throwable, "Error with connectToLockEvent"));
   }

   private void observeBatteryLevel(Janet janet) {
      Observable.combineLatest(
            janet.createPipe(ConnectSmartCardCommand.class)
                  .observeSuccess()
                  .filter(action -> action.getResult().connectionStatus() == CONNECTED),
            activeSmartCardPipe.createObservableResult(new GetActiveSmartCardCommand())
                  .onErrorResumeNext(activeSmartCardPipe.observeSuccessWithReplay().first()),
            (connectCommand, activeCommand) -> connectCommand)
            .subscribe(action -> createBatteryObservable(janet),
                  throwable -> Timber.e(throwable, "Could not schedule battery level requests"));

   }

   private void createBatteryObservable(Janet janet) {
      Observable.interval(0, 1, TimeUnit.MINUTES)
            .takeUntil(janet.createPipe(UnAssignUserAction.class).observe().first())
            .takeUntil(disconnectPipe.observeSuccess())
            .takeUntil(janet.createPipe(ConnectAction.class)
                  .observeSuccess()
                  .filter(action -> action.type == ConnectionType.DFU))
            .doOnNext(o -> fetchBatteryLevelPipe.send(new FetchBatteryLevelCommand()))
            .subscribe();
   }
}
