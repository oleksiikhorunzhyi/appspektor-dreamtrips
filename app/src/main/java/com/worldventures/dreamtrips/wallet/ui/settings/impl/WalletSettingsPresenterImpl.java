package com.worldventures.dreamtrips.wallet.ui.settings.impl;


import android.support.annotation.Nullable;
import android.util.Pair;

import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.SettingsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import java.util.concurrent.TimeUnit;

import rx.Observable;

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
            .compose(bindViewIoToMainComposer())
            .subscribe(pair -> bindSmartCard(pair.first.getResult(), pair.second.getResult()));
   }

   private void bindSmartCard(SmartCardFirmware smartCardFirmware, SmartCardStatus status) {
      getView().smartCardGeneralStatus(smartCardFirmware, status.batteryLevel(), null);
   }

   private void observeFirmwareUpdates() {
      firmwareInteractor.firmwareInfoCachedPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindFirmwareUpdateData(command.getResult()));
   }

   private void bindFirmwareUpdateData(@Nullable FirmwareUpdateData firmwareUpdateData) {
      getView().firmwareUpdateCount(firmwareUpdateData.updateAvailable() ? 1 : 0);
   }
}
