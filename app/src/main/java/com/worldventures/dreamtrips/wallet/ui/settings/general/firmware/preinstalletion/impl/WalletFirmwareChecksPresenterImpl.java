package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateChecksVisitAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateInstallAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateInstallLaterAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.FirmwareChecksState;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksScreen;

import java.util.concurrent.TimeUnit;

import io.techery.janet.smartcard.event.CardInChargerEvent;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.cardIsCharged;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.chargerRequired;

public class WalletFirmwareChecksPresenterImpl extends WalletPresenterImpl<WalletFirmwareChecksScreen> implements WalletFirmwareChecksPresenter{

   private final WalletBluetoothService bluetoothService;
   private final FirmwareInteractor firmwareInteractor;
   private final AnalyticsInteractor analyticsInteractor;

   public WalletFirmwareChecksPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WalletBluetoothService bluetoothService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.bluetoothService = bluetoothService;
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletFirmwareChecksScreen view) {
      super.attachView(view);
      trackScreen();
      observeChecks();
      observeConnection();
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateChecksVisitAction()));
   }

   private void observeChecks() {
      Observable.combineLatest(
            firmwareInteractor.firmwareInfoCachedPipe().observeSuccessWithReplay(),
            bluetoothService.observeEnablesState().startWith(bluetoothService.isEnable()),
            getSmartCardInteractor().deviceStatePipe().observeSuccessWithReplay().throttleLast(500L, TimeUnit.MILLISECONDS),
            getSmartCardInteractor().cardInChargerEventPipe().observeSuccessWithReplay()
                  .startWith(new CardInChargerEvent(false)), // because this event is missing on old firmware versions

            (firmwareInfoCommand, bluetoothEnabled, deviceStateCommand, cardInChargerEvent) ->
                  processResults(firmwareInfoCommand.getResult(),
                        deviceStateCommand.getResult().connectionStatus(),
                        deviceStateCommand.getResult().batteryLevel(),
                        bluetoothEnabled,
                        cardInChargerEvent.inCharger)
      ).compose(bindViewIoToMainComposer())
            .subscribe(this::updateViewStates);

      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
      getSmartCardInteractor().deviceStatePipe().send(DeviceStateCommand.fetch());
      getSmartCardInteractor().fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand());
   }

   private void observeConnection() {
      getSmartCardInteractor().connectionActionPipe()
            .observeSuccess()
            .throttleLast(500L, TimeUnit.MILLISECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(action -> cardConnected());
   }

   private void cardConnected() {
      getSmartCardInteractor().fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand());
   }

   private FirmwareChecksState processResults(FirmwareUpdateData data, ConnectionStatus connectionStatus,
         int batteryLevel, boolean bluetoothEnabled, boolean cardInCharger) {

      if (bluetoothEnabled && connectionStatus == ConnectionStatus.DISCONNECTED) {
         getSmartCardInteractor().connectActionPipe().send(new ConnectSmartCardCommand(data.smartCardId()));
      }

      return new FirmwareChecksState(bluetoothEnabled, connectionStatus.isConnected(),
            cardIsCharged(batteryLevel, cardInCharger), chargerRequired(data.currentFirmwareVersion()), cardInCharger);
   }

   private void updateViewStates(FirmwareChecksState checksState) {
      final WalletFirmwareChecksScreen screen = getView();
      //noinspection ConstantConditions
      screen.bluetoothEnabled(checksState.bluetoothEnable);
      screen.connectionStatusVisible(checksState.bluetoothEnable);
      screen.cardConnected(checksState.cardConnected);
      screen.cardCharged(checksState.charged);
      screen.cardIsInCharger(checksState.cardInCharger);
      screen.chargedStatusVisible(checksState.bluetoothEnable && checksState.cardConnected);

      screen.cardIsInChargerCheckVisible(
            checksState.bluetoothEnable
                  && checksState.cardConnected
                  && checksState.charged
                  && checksState.cardInChargerRequired);
      screen.installButtonEnabled(
            checksState.bluetoothEnable
                  && checksState.cardConnected
                  && checksState.charged
                  && (!checksState.cardInChargerRequired || checksState.cardInCharger));
   }

   @Override
   public void installLater() {
      goBack();
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateInstallLaterAction()));
   }

   @Override
   public void install() {
      getNavigator().goInstallFirmware();
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateInstallAction()));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
