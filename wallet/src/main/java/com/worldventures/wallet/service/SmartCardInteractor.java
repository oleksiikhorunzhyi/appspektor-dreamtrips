package com.worldventures.wallet.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.wallet.analytics.oncard.GetOnCardAnalyticsCommand;
import com.worldventures.wallet.service.command.AboutSmartCardDataCommand;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.wallet.service.command.SetLockStateCommand;
import com.worldventures.wallet.service.command.SetPinEnabledCommand;
import com.worldventures.wallet.service.command.SetSmartCardTimeCommand;
import com.worldventures.wallet.service.command.SetStealthModeCommand;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.RestoreDefaultDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.ValidateDisplayTypeDataCommand;
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;

import java.util.concurrent.Executors;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction;
import io.techery.janet.smartcard.action.records.GetClearRecordsDelayAction;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.action.settings.GetDisableDefaultCardDelayAction;
import io.techery.janet.smartcard.action.settings.GetStealthModeAction;
import io.techery.janet.smartcard.action.settings.RequestPinAuthAction;
import io.techery.janet.smartcard.action.settings.SetPinEnabledAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction;
import io.techery.janet.smartcard.event.CardChargedEvent;
import io.techery.janet.smartcard.event.CardInChargerEvent;
import io.techery.janet.smartcard.event.CardSwipedEvent;
import io.techery.janet.smartcard.event.LockDeviceChangedEvent;
import io.techery.janet.smartcard.event.PinStatusEvent;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public final class SmartCardInteractor {
   private final ActionPipe<ConnectSmartCardCommand> connectionPipe;
   private final ActionPipe<SetSmartCardTimeCommand> setSmartCardTimePipe;
   private final ActionPipe<RestoreOfflineModeDefaultStateCommand> restoreOfflineModeDefaultStatePipe;
   private final ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCardPipe;
   private final ActionPipe<SetStealthModeCommand> stealthModePipe;
   private final ActionPipe<SyncSmartCardCommand> smartCardSyncPipe;
   private final ReadActionPipe<GetStealthModeAction> getStealthModePipe;
   private final ReadActionPipe<LockDeviceChangedEvent> lockDeviceChangedEventPipe;
   private final ActionPipe<SetLockStateCommand> setLockPipe;
   private final ReadActionPipe<GetLockDeviceStatusAction> getLockPipe;
   private final ActionPipe<FetchBatteryLevelCommand> fetchBatteryLevelPipe;
   private final ActionPipe<ActiveSmartCardCommand> activeSmartCardActionPipe;
   private final ActionPipe<DeviceStateCommand> deviceStatePipe;
   private final ActionPipe<SmartCardFirmwareCommand> smartCardFirmwarePipe;
   private final ActionPipe<SmartCardUserCommand> smartCardUserPipe;
   private final ActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<RestartSmartCardCommand> restartSmartCardPipe;
   private final ActionPipe<FetchCardPropertiesCommand> fetchCardPropertiesPipe;
   private final ActionPipe<FetchFirmwareVersionCommand> fetchFirmwareVersionPipe;
   private final ActionPipe<WipeSmartCardDataCommand> wipeSmartCardDataPipe;

   private final ActionPipe<SwitchOfflineModeCommand> switchOfflineModePipe;
   private final ActionPipe<OfflineModeStatusCommand> offlineModeStatusPipe;

   private final ReadActionPipe<CardChargedEvent> chargedEventPipe;
   private final ReadActionPipe<CardSwipedEvent> cardSwipedEventPipe;
   private final ActionPipe<StartCardRecordingAction> startCardRecordingPipe;
   private final ActionPipe<StopCardRecordingAction> stopCardRecordingPipe;

   private final ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe;
   private final ReadActionPipe<GetClearRecordsDelayAction> getAutoClearDelayPipe;
   private final ActionPipe<SetDisableDefaultCardDelayCommand> disableDefaultCardPipe;
   private final ReadActionPipe<GetDisableDefaultCardDelayAction> getDisableDefaultCardDelayPipe;

   private final ActionPipe<GetCompatibleDevicesCommand> compatibleDevicesActionPipe;
   private final ActionPipe<CardInChargerEvent> cardInChargerEventPipe;
   private final ActionPipe<ConnectAction> connectionActionPipe;

   private final ActionPipe<CheckPinStatusAction> checkPinStatusActionPipe;
   private final ActionPipe<SetPinEnabledAction> setPinEnabledActionPipe;
   private final ActionPipe<SetPinEnabledCommand> setPinEnabledCommandActionPipe;
   private final ActionPipe<PinStatusEvent> pinStatusEventPipe;
   private final ActionPipe<RequestPinAuthAction> requestPinAuthActionPipe;
   private final ActionPipe<RemoveUserPhotoAction> removeUserPhotoActionPipe;

   private final ActionPipe<GetOnCardAnalyticsCommand> getOnCardAnalyticsPipe;

   private final ActionPipe<SaveDisplayTypeCommand> saveDisplayTypePipe;
   private final ActionPipe<GetDisplayTypeCommand> getDisplayTypePipe;
   private final ActionPipe<RestoreDefaultDisplayTypeCommand> restoreDefaultDisplayTypePipe;
   private final ActionPipe<ValidateDisplayTypeDataCommand> validateDisplayTypeDataPipe;
   private final ActionPipe<AboutSmartCardDataCommand> aboutSmartCardDataCommandPipe;

   //change to Scheduler Factory
   public SmartCardInteractor(SessionActionPipeCreator sessionActionPipeCreator, WalletSchedulerProvider schedulerProvider) {
      //synchronized pipes
      smartCardSyncPipe = sessionActionPipeCreator.createPipe(SyncSmartCardCommand.class, schedulerProvider.storageScheduler());
      activeSmartCardActionPipe = sessionActionPipeCreator.createPipe(ActiveSmartCardCommand.class, schedulerProvider.storageScheduler());
      deviceStatePipe = sessionActionPipeCreator.createPipe(DeviceStateCommand.class, schedulerProvider.storageScheduler());
      smartCardFirmwarePipe = sessionActionPipeCreator.createPipe(SmartCardFirmwareCommand.class, schedulerProvider.storageScheduler());
      smartCardUserPipe = sessionActionPipeCreator.createPipe(SmartCardUserCommand.class, schedulerProvider.storageScheduler());
      fetchCardPropertiesPipe = sessionActionPipeCreator.createPipe(FetchCardPropertiesCommand.class, schedulerProvider.storageScheduler());
      aboutSmartCardDataCommandPipe = sessionActionPipeCreator.createPipe(AboutSmartCardDataCommand.class, schedulerProvider
            .storageScheduler());

      fetchFirmwareVersionPipe = sessionActionPipeCreator.createPipe(FetchFirmwareVersionCommand.class, Schedulers.io());
      connectionPipe = sessionActionPipeCreator.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      setSmartCardTimePipe = sessionActionPipeCreator.createPipe(SetSmartCardTimeCommand.class, Schedulers.io());
      restoreOfflineModeDefaultStatePipe = sessionActionPipeCreator.createPipe(RestoreOfflineModeDefaultStateCommand.class, Schedulers
            .io());
      fetchAssociatedSmartCardPipe = sessionActionPipeCreator.createPipe(FetchAssociatedSmartCardCommand.class, Schedulers
            .io());
      stealthModePipe = sessionActionPipeCreator.createPipe(SetStealthModeCommand.class, Schedulers.io());
      getStealthModePipe = sessionActionPipeCreator.createPipe(GetStealthModeAction.class); //read action pipe

      lockDeviceChangedEventPipe = sessionActionPipeCreator.createPipe(LockDeviceChangedEvent.class, Schedulers.io());
      setLockPipe = sessionActionPipeCreator.createPipe(SetLockStateCommand.class, Schedulers.io());
      getLockPipe = sessionActionPipeCreator.createPipe(GetLockDeviceStatusAction.class); //read action pipe

      fetchBatteryLevelPipe = sessionActionPipeCreator.createPipe(FetchBatteryLevelCommand.class, Schedulers.io());

      disconnectPipe = sessionActionPipeCreator.createPipe(DisconnectAction.class, Schedulers.io());
      restartSmartCardPipe = sessionActionPipeCreator.createPipe(RestartSmartCardCommand.class, Schedulers.io());
      wipeSmartCardDataPipe = sessionActionPipeCreator.createPipe(WipeSmartCardDataCommand.class, Schedulers
            .io());

      switchOfflineModePipe = sessionActionPipeCreator.createPipe(SwitchOfflineModeCommand.class, Schedulers.io());
      offlineModeStatusPipe = sessionActionPipeCreator.createPipe(OfflineModeStatusCommand.class, Schedulers.io());

      chargedEventPipe = sessionActionPipeCreator.createPipe(CardChargedEvent.class, Schedulers.io());
      cardSwipedEventPipe = sessionActionPipeCreator.createPipe(CardSwipedEvent.class, Schedulers.io());
      startCardRecordingPipe = sessionActionPipeCreator.createPipe(StartCardRecordingAction.class, Schedulers.io());
      stopCardRecordingPipe = sessionActionPipeCreator.createPipe(StopCardRecordingAction.class, Schedulers.io());

      autoClearDelayPipe = sessionActionPipeCreator.createPipe(SetAutoClearSmartCardDelayCommand.class, Schedulers.io());
      getAutoClearDelayPipe = sessionActionPipeCreator.createPipe(GetClearRecordsDelayAction.class); //read action pipe

      disableDefaultCardPipe = sessionActionPipeCreator.createPipe(SetDisableDefaultCardDelayCommand.class, Schedulers.io());
      getDisableDefaultCardDelayPipe = sessionActionPipeCreator.createPipe(GetDisableDefaultCardDelayAction.class); //read action pipe

      cardInChargerEventPipe = sessionActionPipeCreator.createPipe(CardInChargerEvent.class, Schedulers.io());
      compatibleDevicesActionPipe = sessionActionPipeCreator.createPipe(GetCompatibleDevicesCommand.class, Schedulers.io());

      connectionActionPipe = sessionActionPipeCreator.createPipe(ConnectAction.class, Schedulers.io());

      pinStatusEventPipe = sessionActionPipeCreator.createPipe(PinStatusEvent.class);
      checkPinStatusActionPipe = sessionActionPipeCreator.createPipe(CheckPinStatusAction.class, Schedulers.io());
      setPinEnabledActionPipe = sessionActionPipeCreator.createPipe(SetPinEnabledAction.class, Schedulers.io());
      setPinEnabledCommandActionPipe = sessionActionPipeCreator.createPipe(SetPinEnabledCommand.class, Schedulers.io());
      requestPinAuthActionPipe = sessionActionPipeCreator.createPipe(RequestPinAuthAction.class, Schedulers.io());
      removeUserPhotoActionPipe = sessionActionPipeCreator.createPipe(RemoveUserPhotoAction.class, Schedulers.io());

      getOnCardAnalyticsPipe = sessionActionPipeCreator.createPipe(GetOnCardAnalyticsCommand.class, Schedulers.io());

      saveDisplayTypePipe = sessionActionPipeCreator.createPipe(SaveDisplayTypeCommand.class, Schedulers.io());
      getDisplayTypePipe = sessionActionPipeCreator.createPipe(GetDisplayTypeCommand.class, Schedulers.io());
      restoreDefaultDisplayTypePipe = sessionActionPipeCreator.createPipe(RestoreDefaultDisplayTypeCommand.class, Schedulers
            .io());
      validateDisplayTypeDataPipe = sessionActionPipeCreator.createPipe(ValidateDisplayTypeDataCommand.class, Schedulers
            .io());
   }

   private static Scheduler singleThreadScheduler() {
      return Schedulers.from(Executors.newSingleThreadExecutor());
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

   public ActionPipe<SetSmartCardTimeCommand> setSmartCardTimePipe() {
      return setSmartCardTimePipe;
   }

   public ActionPipe<RestoreOfflineModeDefaultStateCommand> restoreOfflineModeDefaultStatePipe() {
      return restoreOfflineModeDefaultStatePipe;
   }

   public ActionPipe<SyncSmartCardCommand> smartCardSyncPipe() {
      return smartCardSyncPipe;
   }

   public ActionPipe<FetchAssociatedSmartCardCommand> fetchAssociatedSmartCard() {
      return fetchAssociatedSmartCardPipe;
   }

   public ActionPipe<SetStealthModeCommand> stealthModePipe() {
      return stealthModePipe;
   }

   public ReadActionPipe<GetStealthModeAction> getStealthModePipe() {
      return getStealthModePipe;
   }

   public ActionPipe<SetLockStateCommand> lockPipe() {
      return setLockPipe;
   }

   public ReadActionPipe<GetLockDeviceStatusAction> getLockPipe() {
      return getLockPipe;
   }

   public ReadActionPipe<LockDeviceChangedEvent> lockDeviceChangedEventPipe() {
      return lockDeviceChangedEventPipe;
   }

   public ActionPipe<FetchCardPropertiesCommand> fetchCardPropertiesPipe() {
      return fetchCardPropertiesPipe;
   }

   public ActionPipe<FetchFirmwareVersionCommand> fetchFirmwareVersionPipe() {
      return fetchFirmwareVersionPipe;
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

   public ActionPipe<DisconnectAction> disconnectPipe() {
      return disconnectPipe;
   }

   public ActionPipe<RestartSmartCardCommand> restartSmartCardPipe() {
      return restartSmartCardPipe;
   }

   public ActionPipe<WipeSmartCardDataCommand> wipeSmartCardDataPipe() {
      return wipeSmartCardDataPipe;
   }

   public ActionPipe<SwitchOfflineModeCommand> switchOfflineModePipe() {
      return switchOfflineModePipe;
   }

   public ActionPipe<OfflineModeStatusCommand> offlineModeStatusPipe() {
      return offlineModeStatusPipe;
   }

   public ReadActionPipe<GetClearRecordsDelayAction> getAutoClearDelayPipe() {
      return getAutoClearDelayPipe;
   }

   public ActionPipe<SetAutoClearSmartCardDelayCommand> autoClearDelayPipe() {
      return autoClearDelayPipe;
   }

   public ReadActionPipe<GetDisableDefaultCardDelayAction> getDisableDefaultCardDelayPipe() {
      return getDisableDefaultCardDelayPipe;
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

   public ActionPipe<CheckPinStatusAction> checkPinStatusActionPipe() {
      return checkPinStatusActionPipe;
   }

   public ActionPipe<SetPinEnabledAction> setPinEnabledActionPipe() {
      return setPinEnabledActionPipe;
   }

   public ActionPipe<SetPinEnabledCommand> setPinEnabledCommandActionPipe() {
      return setPinEnabledCommandActionPipe;
   }

   public ActionPipe<GetOnCardAnalyticsCommand> getOnCardAnalyticsPipe() {
      return getOnCardAnalyticsPipe;
   }

   public ActionPipe<PinStatusEvent> pinStatusEventPipe() {
      return pinStatusEventPipe;
   }

   public ActionPipe<RequestPinAuthAction> requestPinAuthActionPipe() {
      return requestPinAuthActionPipe;
   }

   public ActionPipe<SaveDisplayTypeCommand> saveDisplayTypePipe() {
      return saveDisplayTypePipe;
   }

   public ActionPipe<GetDisplayTypeCommand> getDisplayTypePipe() {
      return getDisplayTypePipe;
   }

   public ActionPipe<RestoreDefaultDisplayTypeCommand> restoreDefaultDisplayTypePipe() {
      return restoreDefaultDisplayTypePipe;
   }

   public ActionPipe<ValidateDisplayTypeDataCommand> validateDisplayTypeDataPipe() {
      return validateDisplayTypeDataPipe;
   }

   public ActionPipe<RemoveUserPhotoAction> removeUserPhotoActionPipe() {
      return removeUserPhotoActionPipe;
   }

   public ActionPipe<AboutSmartCardDataCommand> aboutSmartCardDataCommandPipe() {
      return aboutSmartCardDataCommandPipe;
   }
}
