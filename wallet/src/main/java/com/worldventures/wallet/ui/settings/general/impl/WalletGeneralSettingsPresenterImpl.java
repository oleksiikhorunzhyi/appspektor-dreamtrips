package com.worldventures.wallet.ui.settings.general.impl;


import android.support.annotation.Nullable;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.RestartSmartCardAction;
import com.worldventures.wallet.analytics.settings.SettingsGeneralAction;
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.service.FactoryResetInteractor;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.wallet.ui.settings.general.display.impl.DisplayOptionsSource;
import com.worldventures.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.wallet.util.WalletFeatureHelper;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

public class WalletGeneralSettingsPresenterImpl extends WalletPresenterImpl<WalletGeneralSettingsScreen> implements WalletGeneralSettingsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final FirmwareInteractor firmwareInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WalletFeatureHelper featureHelper;
   private final CheckPinDelegate checkPinDelegate;

   private Action0 firmwareUpdateNavigatorAction = () -> getNavigator().goFirmwareUpToDate();

   public WalletGeneralSettingsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor, FactoryResetInteractor factoryResetInteractor,
         WalletAnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.featureHelper = walletFeatureHelper;
      this.checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.GENERAL);
   }

   @Override
   public void attachView(WalletGeneralSettingsScreen view) {
      super.attachView(view);
      featureHelper.prepareSettingsGeneralScreen(view);
      trackScreen();

      observeSmartCardUserChanges();
      observeFirmwareUpdates();
      checkPinDelegate.observePinStatus(getView());

      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
      smartCardInteractor.smartCardUserPipe().send(SmartCardUserCommand.fetch());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void openProfileScreen() {
      featureHelper.openEditProfile(getView().getViewContext(), this::openProfileAvailable);
   }

   private void openProfileAvailable() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getNavigator().goSettingsProfile();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void openAboutScreen() {
      getNavigator().goSettingsAbout();
   }

   @Override
   public void openSoftwareUpdateScreen() {
      if (firmwareUpdateNavigatorAction != null) {
         firmwareUpdateNavigatorAction.call();
      }
   }

   @Override
   public void openDisplayOptionsScreen() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getNavigator().goSettingsDisplayOptions(DisplayOptionsSource.SETTINGS);
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void onClickFactoryResetSmartCard() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getView().showConfirmFactoryResetDialog();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void openFactoryResetScreen() {
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }

   @Override
   public void openSetupNewSmartCardScreen() {
      getNavigator().goExistingCardDetected();
   }

   @Override
   public void onClickRestartSmartCard() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getView().showConfirmRestartSCDialog();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void onConfirmedRestartSmartCard() {
      smartCardInteractor.restartSmartCardPipe()
            .send(new RestartSmartCardCommand());
      trackSmartCardRestart();
   }

   private void observeSmartCardUserChanges() {
      smartCardInteractor.smartCardUserPipe()
            .observeSuccessWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Command::getResult)
            .subscribe(this::bindSmartCardUser, Timber::e);
   }

   private void bindSmartCardUser(SmartCardUser it) {
      getView().setPreviewPhoto(it.getUserPhoto());
      getView().setUserName(it.getFirstName(), it.getMiddleName(), it.getLastName());
   }

   private void observeFirmwareUpdates() {
      firmwareInteractor.firmwareInfoCachedPipe()
            .observeSuccessWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Command::getResult)
            .subscribe(this::toggleFirmwareBargeOrVersion);
   }

   private void toggleFirmwareBargeOrVersion(@Nullable FirmwareUpdateData firmwareUpdateData) {
      if (firmwareUpdateData != null && firmwareUpdateData.isUpdateAvailable()) {
         getView().firmwareUpdateCount(1);
         getView().showFirmwareBadge();
         firmwareUpdateNavigatorAction = () -> getNavigator().goStartFirmwareInstall();
      } else {
         getView().showFirmwareVersion();
         firmwareUpdateNavigatorAction = () -> getNavigator().goFirmwareUpToDate();
      }
   }

   private void fetchConnectionStatus(Action1<ConnectionStatus> action) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.Companion.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> action.call(command.getResult().getConnectionStatus()))
            );
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new SettingsGeneralAction()));
   }

   private void trackSmartCardRestart() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new RestartSmartCardAction());
      analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand);
   }
}
