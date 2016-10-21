package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardCountCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardModifier;
import com.worldventures.dreamtrips.wallet.service.command.UpdateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateSmartCardConnectionStatus;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateBankCardCommand;

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
import io.techery.janet.smartcard.action.user.UnAssignUserAction;
import io.techery.janet.smartcard.event.CardChargedEvent;
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
   private final ActionPipe<AttachCardCommand> addRecordPipe;
   private final ActionPipe<UpdateBankCardCommand> updateBankCardPipe;
   private final ActionPipe<CardStacksCommand> cardStacksPipe;
   private final ActionPipe<GetActiveSmartCardCommand> activeSmartCardPipe;
   private final ActionPipe<CardCountCommand> cardCountCommandPipe;
   private final ActionPipe<SaveDefaultAddressCommand> saveDefaultAddressPipe;
   private final ActionPipe<GetDefaultAddressCommand> getDefaultAddressCommandPipe;
   private final ActionPipe<SaveCardDetailsDataCommand> saveCardDetailsDataCommandPipe;
   private final ActionPipe<SetStealthModeCommand> stealthModePipe;
   private final ActionPipe<SetLockStateCommand> setLockPipe;
   private final ActionPipe<SaveLockStateCommand> saveLockStatePipe;
   private final ReadActionPipe<LockDeviceChangedEvent> lockDeviceChangedEventPipe;
   private final ReadActionPipe<SmartCardModifier> smartCardModifierPipe;
   private final ActionPipe<FetchDefaultCardIdCommand> fetchDefaultCardIdCommandPipe;
   private final ActionPipe<FetchDefaultCardCommand> fetchDefaultCardCommandPipe;
   private final WriteActionPipe<FetchBatteryLevelCommand> fetchBatteryLevelPipe;
   private final ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe;
   private final ActionPipe<DeleteRecordAction> deleteCardPipe;
   private final ActionPipe<UpdateCardDetailsDataCommand> updateCardDetailsPipe;
   private final ActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<UpdateSmartCardConnectionStatus> updateSmartCardConnectionStatusPipe;

   private final ReadActionPipe<CardChargedEvent> chargedEventPipe;
   private final ActionPipe<StartCardRecordingAction> startCardRecordingPipe;
   private final ActionPipe<StopCardRecordingAction> stopCardRecordingPipe;
   private final ActionPipe<CreateBankCardCommand> recordIssuerInfoPipe;

   private final ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe;
   private final ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardPipe;

   public SmartCardInteractor(@Named(JANET_WALLET) Janet janet) {
      connectionPipe = janet.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      cardsListPipe = janet.createPipe(CardListCommand.class, Schedulers.io());
      addRecordPipe = janet.createPipe(AttachCardCommand.class, Schedulers.io());
      updateBankCardPipe = janet.createPipe(UpdateBankCardCommand.class, Schedulers.io());
      cardStacksPipe = janet.createPipe(CardStacksCommand.class, Schedulers.io());
      activeSmartCardPipe = janet.createPipe(GetActiveSmartCardCommand.class, Schedulers.io());
      stealthModePipe = janet.createPipe(SetStealthModeCommand.class, Schedulers.io());

      smartCardModifierPipe = janet.createPipe(SmartCardModifier.class, Schedulers.io());
      lockDeviceChangedEventPipe = janet.createPipe(LockDeviceChangedEvent.class);
      setLockPipe = janet.createPipe(SetLockStateCommand.class, Schedulers.io());
      saveLockStatePipe = janet.createPipe(SaveLockStateCommand.class, Schedulers.io());

      cardCountCommandPipe = janet.createPipe(CardCountCommand.class, Schedulers.io());
      saveDefaultAddressPipe = janet.createPipe(SaveDefaultAddressCommand.class, Schedulers.io());
      getDefaultAddressCommandPipe = janet.createPipe(GetDefaultAddressCommand.class, Schedulers.io());
      saveCardDetailsDataCommandPipe = janet.createPipe(SaveCardDetailsDataCommand.class, Schedulers.io());
      fetchDefaultCardIdCommandPipe = janet.createPipe(FetchDefaultCardIdCommand.class, Schedulers.io());
      fetchDefaultCardCommandPipe = janet.createPipe(FetchDefaultCardCommand.class, Schedulers.io());
      fetchBatteryLevelPipe = janet.createPipe(FetchBatteryLevelCommand.class, Schedulers.io());
      setDefaultCardOnDeviceCommandPipe = janet.createPipe(SetDefaultCardOnDeviceCommand.class, Schedulers.io());
      deleteCardPipe = janet.createPipe(DeleteRecordAction.class, Schedulers.io());
      updateCardDetailsPipe = janet.createPipe(UpdateCardDetailsDataCommand.class, Schedulers.io());

      disconnectPipe = janet.createPipe(DisconnectAction.class, Schedulers.io());
      updateSmartCardConnectionStatusPipe = janet.createPipe(UpdateSmartCardConnectionStatus.class, Schedulers.io());

      chargedEventPipe = janet.createPipe(CardChargedEvent.class, Schedulers.io());
      startCardRecordingPipe = janet.createPipe(StartCardRecordingAction.class, Schedulers.io());
      stopCardRecordingPipe = janet.createPipe(StopCardRecordingAction.class, Schedulers.io());

      recordIssuerInfoPipe = janet.createPipe(CreateBankCardCommand.class, Schedulers.io());

      autoClearDelayPipe = janet.createPipe(SetAutoClearSmartCardDelayCommand.class, Schedulers.io());
      disableDefaultCardPipe = janet.createPipe(SetDisableDefaultCardDelayCommand.class, Schedulers.io());

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

   public WriteActionPipe<UpdateBankCardCommand> updateBankCardPipe() {
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

   public ActionPipe<GetActiveSmartCardCommand> activeSmartCardPipe() {
      return activeSmartCardPipe;
   }

   public ActionPipe<CardCountCommand> cardCountCommandPipe() {
      return cardCountCommandPipe;
   }

   public ActionPipe<SaveDefaultAddressCommand> saveDefaultAddressPipe() {
      return saveDefaultAddressPipe;
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

   public ActionPipe<SaveCardDetailsDataCommand> saveCardDetailsDataPipe() {
      return saveCardDetailsDataCommandPipe;
   }

   public ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe() {
      return setDefaultCardOnDeviceCommandPipe;
   }

   public ReadActionPipe<CardChargedEvent> chargedEventPipe() {
      return chargedEventPipe;
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

   public ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe() {
      return autoClearDelayPipe;
   }

   public ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardPipe() {
      return disableDefaultCardPipe;
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
   }

   //TODO this way of syncing data between pipes is quite unobvious and should be reworked in future
   private void observeCardsChanges() {
      Observable.merge(
            deleteCardPipe
                  .observeSuccess()
                  .flatMap(deleteCommand -> cardsListPipe.createObservable(remove(valueOf(deleteCommand.recordId)))),
            addRecordPipe
                  .observeSuccess()
                  .flatMap(attachCardCommand -> cardsListPipe.createObservable(add(attachCardCommand.bankCard()))),
            updateBankCardPipe
                  .observeSuccess()
                  .flatMap(updateBankCardCommand -> cardsListPipe.createObservable(edit(updateBankCardCommand.getResult()))),
            updateCardDetailsPipe
                  .observeSuccess()
                  .flatMap(editCardCommand -> cardsListPipe.createObservable(edit(editCardCommand.getResult()))))
            .subscribe(new ActionStateSubscriber<CardListCommand>()
                  .onSuccess(cardListCommand -> cardStacksPipe.send(CardStacksCommand.get(false)))
                  .onFail((cardListCommand, throwable) -> {
                     throw new IllegalStateException("Cannot perform operation onto card list cache", throwable);
                  }));

   }

   private void connectToLockEvent() {
      lockDeviceChangedEventPipe
            .observeSuccess()
            .subscribe(lockDeviceChangedEvent -> {
               saveLockStatePipe.send(new SaveLockStateCommand(lockDeviceChangedEvent.locked));
            });
   }

   private void observeBatteryLevel(Janet janet) {
      janet.createPipe(ConnectSmartCardCommand.class)
            .observeSuccess()
            .filter(action -> action.getResult().connectionStatus() == CONNECTED)
            .flatMap(action -> activeSmartCardPipe.observeSuccessWithReplay().first())
            .subscribe(action -> createBatteryObservable(janet));
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
