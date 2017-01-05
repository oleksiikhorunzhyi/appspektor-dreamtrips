package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AddBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAssociatedSmartCardCommand;

import java.util.concurrent.Executors;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import io.techery.janet.smartcard.event.CardChargedEvent;
import io.techery.janet.smartcard.event.CardInChargerEvent;
import io.techery.janet.smartcard.event.CardSwipedEvent;
import io.techery.janet.smartcard.event.LockDeviceChangedEvent;
import rx.Scheduler;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public final class SmartCardInteractor {
   private final ActionPipe<ConnectSmartCardCommand> connectionPipe;
   private final ActionPipe<CardListCommand> cardsListPipe;
   private final ActionPipe<UpdateBankCardCommand> updateBankCardPipe;
   private final ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCardPipe;
   private final ActionPipe<SyncCardsCommand> syncCardsPipe;
   private final ActionPipe<AttachCardCommand> addRecordPipe;
   private final ActionPipe<GetDefaultAddressCommand> getDefaultAddressCommandPipe;
   private final ActionPipe<AddBankCardCommand> saveCardDetailsDataCommandPipe;
   private final ActionPipe<SetStealthModeCommand> stealthModePipe;
   private final ActionPipe<SetLockStateCommand> setLockPipe;
   private final ActionPipe<FetchBatteryLevelCommand> fetchBatteryLevelPipe;
   private final ReadActionPipe<LockDeviceChangedEvent> lockDeviceChangedEventPipe;
   private final ActionPipe<ActiveSmartCardCommand> activeSmartCardActionPipe;
   private final ActionPipe<DefaultCardIdCommand> defaultCardIdPipe;
   private final ActionPipe<FetchDefaultCardCommand> fetchDefaultCardCommandPipe;
   private final ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe;
   private final ActionPipe<SetPaymentCardAction> setPaymentCardActionActionPipe;
   private final ActionPipe<DeleteRecordAction> deleteCardPipe;
   private final ActionPipe<UpdateCardDetailsDataCommand> updateCardDetailsPipe;
   private final ActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe;
   private final ActionPipe<FetchCardPropertiesCommand> fetchCardPropertiesPipe;

   private final ReadActionPipe<CardChargedEvent> chargedEventPipe;
   private final ReadActionPipe<CardSwipedEvent> cardSwipedEventPipe;
   private final ActionPipe<StartCardRecordingAction> startCardRecordingPipe;
   private final ActionPipe<StopCardRecordingAction> stopCardRecordingPipe;
   private final ActionPipe<CreateBankCardCommand> recordIssuerInfoPipe;

   private final ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe;
   private final ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardPipe;

   private final ActionPipe<GetCompatibleDevicesCommand> compatibleDevicesActionPipe;
   private final ActionPipe<CardInChargerEvent> cardInChargerEventPipe;

   public SmartCardInteractor(Janet janet, SessionActionPipeCreator sessionActionPipeCreator) {
      this(janet, sessionActionPipeCreator, SmartCardInteractor::singleThreadScheduler);
   }

   public SmartCardInteractor(Janet janet, SessionActionPipeCreator sessionActionPipeCreator, Func0<Scheduler> cacheSchedulerFactory) {
      //synchronized pipes
      cardsListPipe = sessionActionPipeCreator.createPipe(CardListCommand.class, cacheSchedulerFactory.call());
      syncCardsPipe = sessionActionPipeCreator.createPipe(SyncCardsCommand.class, cacheSchedulerFactory.call());
      activeSmartCardActionPipe = sessionActionPipeCreator.createPipe(ActiveSmartCardCommand.class, cacheSchedulerFactory
            .call());
      defaultCardIdPipe = sessionActionPipeCreator.createPipe(DefaultCardIdCommand.class, cacheSchedulerFactory.call());
      fetchDefaultCardCommandPipe = sessionActionPipeCreator.createPipe(FetchDefaultCardCommand.class, cacheSchedulerFactory
            .call());
      fetchCardPropertiesPipe = sessionActionPipeCreator.createPipe(FetchCardPropertiesCommand.class, cacheSchedulerFactory
            .call());

      connectionPipe = sessionActionPipeCreator.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      updateBankCardPipe = sessionActionPipeCreator.createPipe(UpdateBankCardCommand.class, Schedulers.io());
      fetchAssociatedSmartCardPipe = sessionActionPipeCreator.createPipe(FetchAssociatedSmartCardCommand.class, Schedulers
            .io());
      stealthModePipe = sessionActionPipeCreator.createPipe(SetStealthModeCommand.class, Schedulers.io());
      addRecordPipe = sessionActionPipeCreator.createPipe(AttachCardCommand.class, Schedulers.io());

      lockDeviceChangedEventPipe = sessionActionPipeCreator.createPipe(LockDeviceChangedEvent.class, Schedulers.io());
      setLockPipe = sessionActionPipeCreator.createPipe(SetLockStateCommand.class, Schedulers.io());
      fetchBatteryLevelPipe = sessionActionPipeCreator.createPipe(FetchBatteryLevelCommand.class, Schedulers.io());

      getDefaultAddressCommandPipe = sessionActionPipeCreator.createPipe(GetDefaultAddressCommand.class, Schedulers.io());
      saveCardDetailsDataCommandPipe = sessionActionPipeCreator.createPipe(AddBankCardCommand.class, Schedulers.io());
      setDefaultCardOnDeviceCommandPipe = sessionActionPipeCreator.createPipe(SetDefaultCardOnDeviceCommand.class, Schedulers
            .io());
      setPaymentCardActionActionPipe = sessionActionPipeCreator.createPipe(SetPaymentCardAction.class, Schedulers.io());
      deleteCardPipe = sessionActionPipeCreator.createPipe(DeleteRecordAction.class, Schedulers.io());
      updateCardDetailsPipe = sessionActionPipeCreator.createPipe(UpdateCardDetailsDataCommand.class, Schedulers.io());

      disconnectPipe = sessionActionPipeCreator.createPipe(DisconnectAction.class, Schedulers.io());
      restartSmartCardCommandActionPipe = sessionActionPipeCreator.createPipe(RestartSmartCardCommand.class, Schedulers.io());


      chargedEventPipe = sessionActionPipeCreator.createPipe(CardChargedEvent.class, Schedulers.io());
      cardSwipedEventPipe = sessionActionPipeCreator.createPipe(CardSwipedEvent.class, Schedulers.io());
      startCardRecordingPipe = sessionActionPipeCreator.createPipe(StartCardRecordingAction.class, Schedulers.io());
      stopCardRecordingPipe = sessionActionPipeCreator.createPipe(StopCardRecordingAction.class, Schedulers.io());

      recordIssuerInfoPipe = sessionActionPipeCreator.createPipe(CreateBankCardCommand.class, Schedulers.io());

      autoClearDelayPipe = sessionActionPipeCreator.createPipe(SetAutoClearSmartCardDelayCommand.class, Schedulers.io());
      disableDefaultCardPipe = sessionActionPipeCreator.createPipe(SetDisableDefaultCardDelayCommand.class, Schedulers.io());

      cardInChargerEventPipe = sessionActionPipeCreator.createPipe(CardInChargerEvent.class, Schedulers.io());
      compatibleDevicesActionPipe = sessionActionPipeCreator.createPipe(GetCompatibleDevicesCommand.class, Schedulers.io());
      //
      new SmartCardSyncManager(janet, this);// start sync when start use the wallet
   }

   private static Scheduler singleThreadScheduler() {
      return Schedulers.from(Executors.newSingleThreadExecutor());
   }

   public ActionPipe<CardListCommand> cardsListPipe() {
      return cardsListPipe;
   }

   public ActionPipe<ActiveSmartCardCommand> activeSmartCardPipe() {
      return activeSmartCardActionPipe;
   }

   public ActionPipe<ConnectSmartCardCommand> connectActionPipe() {
      return connectionPipe;
   }

   public ActionPipe<UpdateBankCardCommand> updateBankCardPipe() {
      return updateBankCardPipe;
   }

   public ActionPipe<SyncCardsCommand> cardSyncPipe() {
      return syncCardsPipe;
   }

   public ActionPipe<DefaultCardIdCommand> defaultCardIdPipe() {
      return defaultCardIdPipe;
   }

   public ActionPipe<FetchDefaultCardCommand> fetchDefaultCardCommandPipe() {
      return fetchDefaultCardCommandPipe;
   }

   public ActionPipe<DeleteRecordAction> deleteCardPipe() {
      return deleteCardPipe;
   }

   public ActionPipe<UpdateCardDetailsDataCommand> updateCardDetailsPipe() {
      return updateCardDetailsPipe;
   }

   public ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCard() {
      return fetchAssociatedSmartCardPipe;
   }

   public ActionPipe<AttachCardCommand> addRecordPipe() {
      return addRecordPipe;
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

   public ActionPipe<FetchCardPropertiesCommand> fetchCardPropertiesPipe() {
      return fetchCardPropertiesPipe;
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

   public ActionPipe<DisconnectAction> disconnectPipe() {
      return disconnectPipe;
   }

   public ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe() {
      return restartSmartCardCommandActionPipe;
   }

   public ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe() {
      return autoClearDelayPipe;
   }

   public ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardDelayPipe() {
      return disableDefaultCardPipe;
   }

   public ActionPipe<GetCompatibleDevicesCommand> compatibleDevicesActionPipe() {
      return compatibleDevicesActionPipe;
   }

   public ActionPipe<FetchBatteryLevelCommand> fetchBatteryLevelPipe() {
      return fetchBatteryLevelPipe;
   }

   public ActionPipe<CardInChargerEvent> cardInChargerEventPipe() {
      return cardInChargerEventPipe;
   }

}
