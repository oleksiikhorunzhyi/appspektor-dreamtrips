package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
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
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.smartcard.event.CardInChargerEvent;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.cardIsCharged;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.chargerRequered;

public class WalletFirmwareChecksPresenter extends WalletPresenter<WalletFirmwareChecksPresenter.Screen, Parcelable> {

   @Inject WalletBluetoothService bluetoothService;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Navigator navigator;

   WalletFirmwareChecksPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
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
            smartCardInteractor.deviceStatePipe().observeSuccessWithReplay().throttleLast(500L, TimeUnit.MILLISECONDS),
            smartCardInteractor.cardInChargerEventPipe().observeSuccessWithReplay()
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
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.fetch());
      smartCardInteractor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand());
   }

   private void observeConnection() {
      smartCardInteractor.connectionActionPipe()
            .observeSuccess()
            .throttleLast(500L, TimeUnit.MILLISECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(action -> cardConnected());
   }

   private void cardConnected() {
      smartCardInteractor.fetchBatteryLevelPipe().send(new FetchBatteryLevelCommand());
   }

   private FirmwareChecksState processResults(FirmwareUpdateData data, ConnectionStatus connectionStatus,
         int batteryLevel, boolean bluetoothEnabled, boolean cardInCharger) {

      if (bluetoothEnabled && connectionStatus == ConnectionStatus.DISCONNECTED) {
         smartCardInteractor.connectActionPipe().send(new ConnectSmartCardCommand(data.smartCardId()));
      }

      return new FirmwareChecksState(bluetoothEnabled, connectionStatus.isConnected(),
            cardIsCharged(batteryLevel), chargerRequered(data), cardInCharger);
   }

   private void updateViewStates(FirmwareChecksState checksState) {
      Screen screen = getView();
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

   void installLater() {
      goBack();
      analyticsInteractor.walletFirmwareAnalyticsPipe()
                  .send(new WalletFirmwareAnalyticsCommand(new UpdateInstallLaterAction()));
   }

   void install() {
      navigator.go(new WalletInstallFirmwarePath());
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateInstallAction()));
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void bluetoothEnabled(boolean enabled);

      void cardConnected(boolean connected);

      void cardCharged(boolean charged);

      void connectionStatusVisible(boolean isVisible);

      void chargedStatusVisible(boolean isVisible);

      void installButtonEnabled(boolean enabled);

      void cardIsInCharger(boolean enabled);

      void cardIsInChargerCheckVisible(boolean isVisible);
   }
}
