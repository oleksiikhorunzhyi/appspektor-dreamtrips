package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.analytics.oncard.GetOnCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetPinEnabledCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPinEnabledCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;

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
import io.techery.janet.smartcard.action.settings.SetPinEnabledAction;
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
   private final ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe;
   private final ActionPipe<FetchCardPropertiesCommand> fetchCardPropertiesPipe;
   private final ActionPipe<FetchFirmwareVersionCommand> fetchFirmwareVersionPipe;
   private final ActionPipe<WipeSmartCardDataCommand> wipeSmartCardDataOnBackedCommandActionPipe;

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
   private final ActionPipe<GetPinEnabledCommand> getPinEnabledCommandActionPipe;
   private final ActionPipe<SetPinEnabledCommand> setPinEnabledCommandActionPipe;

   private final ActionPipe<GetOnCardAnalyticsCommand> getOnCardAnalyticsPipe;

   public SmartCardInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this(sessionActionPipeCreator, SmartCardInteractor::singleThreadScheduler);
   }

   public SmartCardInteractor(SessionActionPipeCreator sessionActionPipeCreator, Func0<Scheduler> cacheSchedulerFactory) {
      //synchronized pipes
      smartCardSyncPipe = sessionActionPipeCreator.createPipe(SyncSmartCardCommand.class, cacheSchedulerFactory.call());
      activeSmartCardActionPipe = sessionActionPipeCreator.createPipe(ActiveSmartCardCommand.class, cacheSchedulerFactory
            .call());
      deviceStatePipe = sessionActionPipeCreator.createPipe(DeviceStateCommand.class, cacheSchedulerFactory.call());
      smartCardFirmwarePipe = sessionActionPipeCreator.createPipe(SmartCardFirmwareCommand.class, cacheSchedulerFactory.call());
      smartCardUserPipe = sessionActionPipeCreator.createPipe(SmartCardUserCommand.class, cacheSchedulerFactory.call());
      fetchCardPropertiesPipe = sessionActionPipeCreator.createPipe(FetchCardPropertiesCommand.class, cacheSchedulerFactory
            .call());

      fetchFirmwareVersionPipe = sessionActionPipeCreator.createPipe(FetchFirmwareVersionCommand.class, Schedulers.io());
      connectionPipe = sessionActionPipeCreator.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
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
      restartSmartCardCommandActionPipe = sessionActionPipeCreator.createPipe(RestartSmartCardCommand.class, Schedulers.io());
      wipeSmartCardDataOnBackedCommandActionPipe = sessionActionPipeCreator.createPipe(WipeSmartCardDataCommand.class, Schedulers
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

      checkPinStatusActionPipe = sessionActionPipeCreator.createPipe(CheckPinStatusAction.class, Schedulers.io());
      setPinEnabledActionPipe = sessionActionPipeCreator.createPipe(SetPinEnabledAction.class, Schedulers.io());
      getPinEnabledCommandActionPipe = sessionActionPipeCreator.createPipe(GetPinEnabledCommand.class, Schedulers.io());
      setPinEnabledCommandActionPipe = sessionActionPipeCreator.createPipe(SetPinEnabledCommand.class, Schedulers.io());

      getOnCardAnalyticsPipe = sessionActionPipeCreator.createPipe(GetOnCardAnalyticsCommand.class, Schedulers.io());
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

   public ActionPipe<RestartSmartCardCommand> restartSmartCardCommandActionPipe() {
      return restartSmartCardCommandActionPipe;
   }

   public ActionPipe<WipeSmartCardDataCommand> wipeSmartCardDataCommandActionPipe() {
      return wipeSmartCardDataOnBackedCommandActionPipe;
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

   public ActionPipe<GetPinEnabledCommand> getPinEnabledCommandActionPipe() {
      return getPinEnabledCommandActionPipe;
   }

   public ActionPipe<SetPinEnabledCommand> setPinEnabledCommandActionPipe() {
      return setPinEnabledCommandActionPipe;
   }

   public ActionPipe<GetOnCardAnalyticsCommand> getOnCardAnalyticsPipe() {
      return getOnCardAnalyticsPipe;
   }

}
