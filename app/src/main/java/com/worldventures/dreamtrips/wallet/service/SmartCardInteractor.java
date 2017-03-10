package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.CardInChargerCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;

import java.util.concurrent.Executors;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
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
   private final ActionPipe<RecordListCommand> cardsListPipe;
   private final ActionPipe<UpdateRecordCommand> updateRecordPipe;
   private final ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCardPipe;
   private final ActionPipe<SyncRecordsCommand> syncRecordsPipe;
   private final ActionPipe<AttachCardCommand> addRecordPipe;
   private final WriteActionPipe<AddRecordAction> addNativeRecordPipe;
   private final ActionPipe<GetDefaultAddressCommand> getDefaultAddressCommandPipe;
   private final ActionPipe<AddRecordCommand> saveCardDetailsDataCommandPipe;
   private final ActionPipe<SetStealthModeCommand> stealthModePipe;
   private final ActionPipe<SetLockStateCommand> setLockPipe;
   private final ActionPipe<FetchBatteryLevelCommand> fetchBatteryLevelPipe;
   private final ReadActionPipe<LockDeviceChangedEvent> lockDeviceChangedEventPipe;
   private final ActionPipe<ActiveSmartCardCommand> activeSmartCardActionPipe;
   private final ActionPipe<DeviceStateCommand> deviceStatePipe;
   private final ActionPipe<SmartCardFirmwareCommand> smartCardFirmwarePipe;
   private final ActionPipe<SmartCardUserCommand> smartCardUserPipe;
   private final ActionPipe<DefaultRecordIdCommand> defaultRecordIdPipe;
   private final ActionPipe<FetchDefaultRecordCommand> fetchDefaultCardCommandPipe;
   private final ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe;
   private final ActionPipe<SetPaymentCardAction> setPaymentCardActionActionPipe;
   private final ActionPipe<DeleteRecordAction> deleteRecordPipe;
   private final ActionPipe<UpdateCardDetailsDataCommand> updateCardDetailsPipe;
   private final ActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe;
   private final ActionPipe<FetchCardPropertiesCommand> fetchCardPropertiesPipe;
   private final ActionPipe<FetchFirmwareVersionCommand> fetchFirmwareVersionPipe;
   private final ActionPipe<WipeSmartCardDataCommand> wipeSmartCardDataOnBackedCommandActionPipe;

   private final ReadActionPipe<CardChargedEvent> chargedEventPipe;
   private final ReadActionPipe<CardSwipedEvent> cardSwipedEventPipe;
   private final ActionPipe<StartCardRecordingAction> startCardRecordingPipe;
   private final ActionPipe<StopCardRecordingAction> stopCardRecordingPipe;
   private final ActionPipe<CreateRecordCommand> recordIssuerInfoPipe;

   private final ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe;
   private final ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardPipe;

   private final ActionPipe<GetCompatibleDevicesCommand> compatibleDevicesActionPipe;
   private final ActionPipe<CardInChargerEvent> cardInChargerEventPipe;
   private final ActionPipe<ConnectAction> connectionActionPipe;
   private final ActionPipe<CardInChargerCommand> cardInChargerCommandActionPipe;

   public SmartCardInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this(sessionActionPipeCreator, SmartCardInteractor::singleThreadScheduler);
   }

   public SmartCardInteractor(SessionActionPipeCreator sessionActionPipeCreator, Func0<Scheduler> cacheSchedulerFactory) {
      //synchronized pipes
      cardsListPipe = sessionActionPipeCreator.createPipe(RecordListCommand.class, cacheSchedulerFactory.call());
      syncRecordsPipe = sessionActionPipeCreator.createPipe(SyncRecordsCommand.class, cacheSchedulerFactory.call());
      activeSmartCardActionPipe = sessionActionPipeCreator.createPipe(ActiveSmartCardCommand.class, cacheSchedulerFactory
            .call());
      deviceStatePipe = sessionActionPipeCreator.createPipe(DeviceStateCommand.class, cacheSchedulerFactory.call());
      smartCardFirmwarePipe = sessionActionPipeCreator.createPipe(SmartCardFirmwareCommand.class, cacheSchedulerFactory.call());
      smartCardUserPipe = sessionActionPipeCreator.createPipe(SmartCardUserCommand.class, cacheSchedulerFactory.call());

      defaultRecordIdPipe = sessionActionPipeCreator.createPipe(DefaultRecordIdCommand.class, cacheSchedulerFactory.call());
      fetchDefaultCardCommandPipe = sessionActionPipeCreator.createPipe(FetchDefaultRecordCommand.class, cacheSchedulerFactory
            .call());
      fetchCardPropertiesPipe = sessionActionPipeCreator.createPipe(FetchCardPropertiesCommand.class, cacheSchedulerFactory
            .call());

      fetchFirmwareVersionPipe = sessionActionPipeCreator.createPipe(FetchFirmwareVersionCommand.class, Schedulers.io());
      connectionPipe = sessionActionPipeCreator.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      updateRecordPipe = sessionActionPipeCreator.createPipe(UpdateRecordCommand.class, Schedulers.io());
      fetchAssociatedSmartCardPipe = sessionActionPipeCreator.createPipe(FetchAssociatedSmartCardCommand.class, Schedulers
            .io());
      stealthModePipe = sessionActionPipeCreator.createPipe(SetStealthModeCommand.class, Schedulers.io());
      addRecordPipe = sessionActionPipeCreator.createPipe(AttachCardCommand.class, Schedulers.io());
      addNativeRecordPipe = sessionActionPipeCreator.createPipe(AddRecordAction.class);

      lockDeviceChangedEventPipe = sessionActionPipeCreator.createPipe(LockDeviceChangedEvent.class, Schedulers.io());
      setLockPipe = sessionActionPipeCreator.createPipe(SetLockStateCommand.class, Schedulers.io());
      fetchBatteryLevelPipe = sessionActionPipeCreator.createPipe(FetchBatteryLevelCommand.class, Schedulers.io());

      getDefaultAddressCommandPipe = sessionActionPipeCreator.createPipe(GetDefaultAddressCommand.class, Schedulers.io());
      saveCardDetailsDataCommandPipe = sessionActionPipeCreator.createPipe(AddRecordCommand.class, Schedulers.io());
      setDefaultCardOnDeviceCommandPipe = sessionActionPipeCreator.createPipe(SetDefaultCardOnDeviceCommand.class, Schedulers
            .io());
      setPaymentCardActionActionPipe = sessionActionPipeCreator.createPipe(SetPaymentCardAction.class, Schedulers.io());
      deleteRecordPipe = sessionActionPipeCreator.createPipe(DeleteRecordAction.class, Schedulers.io());
      updateCardDetailsPipe = sessionActionPipeCreator.createPipe(UpdateCardDetailsDataCommand.class, Schedulers.io());

      disconnectPipe = sessionActionPipeCreator.createPipe(DisconnectAction.class, Schedulers.io());
      restartSmartCardCommandActionPipe = sessionActionPipeCreator.createPipe(RestartSmartCardCommand.class, Schedulers.io());
      wipeSmartCardDataOnBackedCommandActionPipe = sessionActionPipeCreator.createPipe(WipeSmartCardDataCommand.class, Schedulers
            .io());


      chargedEventPipe = sessionActionPipeCreator.createPipe(CardChargedEvent.class, Schedulers.io());
      cardSwipedEventPipe = sessionActionPipeCreator.createPipe(CardSwipedEvent.class, Schedulers.io());
      startCardRecordingPipe = sessionActionPipeCreator.createPipe(StartCardRecordingAction.class, Schedulers.io());
      stopCardRecordingPipe = sessionActionPipeCreator.createPipe(StopCardRecordingAction.class, Schedulers.io());

      recordIssuerInfoPipe = sessionActionPipeCreator.createPipe(CreateRecordCommand.class, Schedulers.io());

      autoClearDelayPipe = sessionActionPipeCreator.createPipe(SetAutoClearSmartCardDelayCommand.class, Schedulers.io());
      disableDefaultCardPipe = sessionActionPipeCreator.createPipe(SetDisableDefaultCardDelayCommand.class, Schedulers.io());

      cardInChargerEventPipe = sessionActionPipeCreator.createPipe(CardInChargerEvent.class, Schedulers.io());
      compatibleDevicesActionPipe = sessionActionPipeCreator.createPipe(GetCompatibleDevicesCommand.class, Schedulers.io());

      connectionActionPipe = sessionActionPipeCreator.createPipe(ConnectAction.class, Schedulers.io());
      cardInChargerCommandActionPipe = sessionActionPipeCreator.createPipe(CardInChargerCommand.class, Schedulers.io());
   }

   private static Scheduler singleThreadScheduler() {
      return Schedulers.from(Executors.newSingleThreadExecutor());
   }

   public ActionPipe<RecordListCommand> cardsListPipe() {
      return cardsListPipe;
   }

   public ActionPipe<ActiveSmartCardCommand> activeSmartCardPipe() {
      return activeSmartCardActionPipe;
   }

   public ActionPipe<DeviceStateCommand> deviceStatePipe() {
      return deviceStatePipe;
   }

   public ActionPipe<SmartCardFirmwareCommand> smartCardFirmwarePipe() {
      return smartCardFirmwarePipe;
   }

   public ActionPipe<SmartCardUserCommand> smartCardUserPipe() {
      return smartCardUserPipe;
   }

   public ActionPipe<ConnectSmartCardCommand> connectActionPipe() {
      return connectionPipe;
   }

   public ActionPipe<UpdateRecordCommand> updateRecordPipe() {
      return updateRecordPipe;
   }

   public ActionPipe<SyncRecordsCommand> recordsSyncPipe() {
      return syncRecordsPipe;
   }

   public ActionPipe<DefaultRecordIdCommand> defaultRecordIdPipe() {
      return defaultRecordIdPipe;
   }

   public ActionPipe<FetchDefaultRecordCommand> fetchDefaultCardCommandPipe() {
      return fetchDefaultCardCommandPipe;
   }

   public ActionPipe<DeleteRecordAction> deleteRecordPipe() {
      return deleteRecordPipe;
   }

   @Deprecated
   public ActionPipe<UpdateCardDetailsDataCommand> updateCardDetailsPipe() {
      return updateCardDetailsPipe;
   }

   public ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCard() {
      return fetchAssociatedSmartCardPipe;
   }

   public ActionPipe<AttachCardCommand> addRecordPipe() {
      return addRecordPipe;
   }

   public WriteActionPipe<AddRecordAction> addNativeRecordPipe() {
      return addNativeRecordPipe;
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

   public ActionPipe<AddRecordCommand> saveCardDetailsDataPipe() {
      return saveCardDetailsDataCommandPipe;
   }

   public ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe() {
      return setDefaultCardOnDeviceCommandPipe;
   }

   public ActionPipe<FetchCardPropertiesCommand> fetchCardPropertiesPipe() {
      return fetchCardPropertiesPipe;
   }

   public ActionPipe<FetchFirmwareVersionCommand> fetchFirmwareVersionPipe() {
      return fetchFirmwareVersionPipe;
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


   public ActionPipe<CreateRecordCommand> bankCardPipe() {
      return recordIssuerInfoPipe;
   }

   public ActionPipe<DisconnectAction> disconnectPipe() {
      return disconnectPipe;
   }

   public ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe() {
      return restartSmartCardCommandActionPipe;
   }

   public ActionPipe<WipeSmartCardDataCommand> wipeSmartCardDataCommandActionPipe() {
      return wipeSmartCardDataOnBackedCommandActionPipe;
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

   public ActionPipe<ConnectAction> connectionActionPipe() {
      return connectionActionPipe;
   }

   public ActionPipe<CardInChargerCommand> cardInChargerCommandActionPipe() {
      return cardInChargerCommandActionPipe;
   }
}
