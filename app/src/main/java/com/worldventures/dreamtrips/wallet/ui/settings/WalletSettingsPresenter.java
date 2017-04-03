package com.worldventures.dreamtrips.wallet.ui.settings;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.WalletSettingsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.help.WalletHelpSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPath;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;

public class WalletSettingsPresenter extends WalletPresenter<WalletSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WalletSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      trackScreen();

      observeSmartCardChanges();
      observeFirmwareUpdates();

      smartCardInteractor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.fetch());
      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
   }

   public void goBack() {
      navigator.goBack();
   }

   void openGeneralScreen() {
      navigator.go(new WalletGeneralSettingsPath());
   }

   void openSecurityScreen() {
      navigator.go(new WalletSecuritySettingsPath());
   }

   void openHelpScreen() {
      navigator.go(new WalletHelpSettingsPath());
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new WalletSettingsAction()));
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

   public interface Screen extends WalletScreen {

      void smartCardGeneralStatus(@Nullable SmartCardFirmware version, int batteryLevel, Date lastSync);

      void firmwareUpdateCount(int count);

   }
}
