package com.worldventures.wallet.ui.settings.impl;


import android.support.annotation.Nullable;
import android.util.Pair;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.SettingsAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.entity.SmartCardStatus;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.wallet.util.WalletFeatureHelper;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WalletSettingsPresenterImpl extends WalletPresenterImpl<WalletSettingsScreen> implements WalletSettingsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final FirmwareInteractor firmwareInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WalletFeatureHelper featureHelper;

   public WalletSettingsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor,
         WalletAnalyticsInteractor analyticsInteractor, WalletFeatureHelper featureHelper) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.featureHelper = featureHelper;
   }

   @Override
   public void attachView(WalletSettingsScreen view) {
      super.attachView(view);
      featureHelper.prepareSettingsScreen(view);
      trackScreen();

      observeSmartCardChanges();
      observeFirmwareUpdates();

      smartCardInteractor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.fetch());
      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void openGeneralScreen() {
      getNavigator().goSettingsGeneral();
   }

   @Override
   public void openSecurityScreen() {
      getNavigator().goSettingsSecurity();
   }

   @Override
   public void openHelpScreen() {
      getNavigator().goSettingsHelp();
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new SettingsAction()));
   }

   private void observeSmartCardChanges() {
      Observable.combineLatest(
            smartCardInteractor.smartCardFirmwarePipe().observeSuccessWithReplay(),
            smartCardInteractor.deviceStatePipe().observeSuccessWithReplay(),
            Pair::new)
            .throttleLast(200, TimeUnit.MILLISECONDS)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pair -> bindSmartCard(pair.first.getResult(), pair.second.getResult()));
   }

   private void bindSmartCard(SmartCardFirmware smartCardFirmware, SmartCardStatus status) {
      getView().smartCardGeneralStatus(smartCardFirmware, status.batteryLevel(), null);
   }

   private void observeFirmwareUpdates() {
      firmwareInteractor.firmwareInfoCachedPipe()
            .observeSuccessWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> bindFirmwareUpdateData(command.getResult()));
   }

   private void bindFirmwareUpdateData(@Nullable FirmwareUpdateData firmwareUpdateData) {
      getView().firmwareUpdateCount(firmwareUpdateData.updateAvailable() ? 1 : 0);
   }
}
