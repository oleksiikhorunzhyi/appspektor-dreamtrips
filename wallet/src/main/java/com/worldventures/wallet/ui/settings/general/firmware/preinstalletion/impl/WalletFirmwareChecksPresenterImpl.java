package com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.impl;


import com.worldventures.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.wallet.analytics.firmware.action.UpdateChecksVisitAction;
import com.worldventures.wallet.analytics.firmware.action.UpdateInstallAction;
import com.worldventures.wallet.analytics.firmware.action.UpdateInstallLaterAction;
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.FirmwareChecksState;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksScreen;

import java.util.concurrent.TimeUnit;

import io.techery.janet.smartcard.event.CardInChargerEvent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.wallet.util.SCFirmwareUtils.cardIsCharged;
import static com.worldventures.wallet.util.SCFirmwareUtils.chargerRequired;

public class WalletFirmwareChecksPresenterImpl extends WalletPresenterImpl<WalletFirmwareChecksScreen> implements WalletFirmwareChecksPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletBluetoothService bluetoothService;
   private final FirmwareInteractor firmwareInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public WalletFirmwareChecksPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletBluetoothService bluetoothService, FirmwareInteractor firmwareInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
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
            smartCardInteractor.deviceStatePipe()
                  .observeSuccessWithReplay()
                  .throttleLast(500L, TimeUnit.MILLISECONDS),
            smartCardInteractor.cardInChargerEventPipe().observeSuccessWithReplay()
                  .startWith(new CardInChargerEvent(false)), // because this event is missing on old firmware versions

            (firmwareInfoCommand, bluetoothEnabled, deviceStateCommand, cardInChargerEvent) ->
                  processResults(firmwareInfoCommand.getResult(),
                        deviceStateCommand.getResult().getConnectionStatus(),
                        deviceStateCommand.getResult().getBatteryLevel(),
                        bluetoothEnabled,
                        cardInChargerEvent.inCharger)
      ).compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateViewStates);

      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.Companion.fetch());
      smartCardInteractor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand());
   }

   private void observeConnection() {
      smartCardInteractor.connectionActionPipe()
            .observeSuccess()
            .throttleLast(500L, TimeUnit.MILLISECONDS)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action -> cardConnected());
   }

   private void cardConnected() {
      smartCardInteractor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand());
   }

   private FirmwareChecksState processResults(FirmwareUpdateData data, ConnectionStatus connectionStatus,
         int batteryLevel, boolean bluetoothEnabled, boolean cardInCharger) {

      if (bluetoothEnabled && connectionStatus == ConnectionStatus.DISCONNECTED) {
         smartCardInteractor.connectActionPipe().send(new ConnectSmartCardCommand(data.getSmartCardId()));
      }

      return new FirmwareChecksState(bluetoothEnabled, connectionStatus.isConnected(),
            cardIsCharged(batteryLevel, cardInCharger), chargerRequired(data.getCurrentFirmwareVersion()), cardInCharger);
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
