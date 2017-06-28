package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.RestartSmartCardAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SettingsGeneralAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.AboutPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import javax.inject.Inject;

import flow.path.Path;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;
import timber.log.Timber;

public class WalletGeneralSettingsPresenter extends WalletPresenter<WalletGeneralSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WalletFeatureHelper featureHelper;

   private final CheckPinDelegate checkPinDelegate;

   private Path firmwareUpdatePath = new WalletUpToDateFirmwarePath();

   public WalletGeneralSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
      checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.GENERAL);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      featureHelper.prepareSettingsGeneralScreen(view);
      trackScreen();

      observeSmartCardUserChanges();
      observeFirmwareUpdates();
      checkPinDelegate.observePinStatus(getView());

      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
      smartCardInteractor.smartCardUserPipe().send(SmartCardUserCommand.fetch());
   }

   public void goBack() {
      navigator.goBack();
   }

   void openProfileScreen() {
      featureHelper.openEditProfile(getContext(), this::openProfileAvailable);
   }

   private void openProfileAvailable() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            navigator.go(new WalletSettingsProfilePath());
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void openAboutScreen() {
      navigator.go(new AboutPath());
   }

   void openSoftwareUpdateScreen() {
      if (firmwareUpdatePath != null) {
         navigator.go(firmwareUpdatePath);
      }
   }

   void openDisplayOptionsScreen() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            fetchSmartCardUser(scUser -> navigator.go(new DisplayOptionsSettingsPath(scUser, DisplayOptionsSource.SETTINGS)));
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void onClickFactoryResetSmartCard() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getView().showConfirmFactoryResetDialog();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void openFactoryResetScreen() {
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }

   void openSetupNewSmartCardScreen() {
      navigator.go(new ExistingCardDetectPath());
   }

   void onClickRestartSmartCard() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getView().showConfirmRestartSCDialog();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void onConfirmedRestartSmartCard() {
      smartCardInteractor.restartSmartCardCommandActionPipe()
            .send(new RestartSmartCardCommand());
      trackSmartCardRestart();
   }

   private void observeSmartCardUserChanges() {
      smartCardInteractor.smartCardUserPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(this::bindSmartCardUser, throwable -> Timber.e(throwable, ""));
   }

   private void bindSmartCardUser(SmartCardUser it) {
      getView().setPreviewPhoto(it.userPhoto());
      getView().setUserName(it.firstName(), it.middleName(), it.lastName());
   }

   private void observeFirmwareUpdates() {
      firmwareInteractor.firmwareInfoCachedPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(this::toggleFirmwareBargeOrVersion);
   }

   private void toggleFirmwareBargeOrVersion(@Nullable FirmwareUpdateData firmwareUpdateData) {
      if (firmwareUpdateData != null && firmwareUpdateData.updateAvailable()) {
         getView().firmwareUpdateCount(1);
         getView().showFirmwareBadge();
         firmwareUpdatePath = new StartFirmwareInstallPath();
      } else {
         getView().showFirmwareVersion();
         firmwareUpdatePath = new WalletUpToDateFirmwarePath();
      }
   }

   private void fetchConnectionStatus(Action1<ConnectionStatus> action) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> action.call(command.getResult().connectionStatus()))
            );
   }

   private void fetchSmartCardUser(Action1<SmartCardUser> userAction1) {
      smartCardInteractor.smartCardUserPipe()
            .createObservable(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess(command -> userAction1.call(command.getResult()))
            );
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new SettingsGeneralAction()));
   }

   private void trackSmartCardRestart() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new RestartSmartCardAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   public interface Screen extends WalletScreen, FactoryResetView {

      void setPreviewPhoto(@Nullable SmartCardUserPhoto photo);

      void setUserName(String firstName, String middleName, String lastName);

      void firmwareUpdateCount(int count);

      void showFirmwareVersion();

      void showFirmwareBadge();

      void showSCNonConnectionDialog();

      void showConfirmFactoryResetDialog();

      void showConfirmRestartSCDialog();
   }

}
