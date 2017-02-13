package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.about.AboutPath;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset.FactoryResetPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.start.StartFirmwareInstallPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate.WalletUpToDateFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.unassign.ExistingCardDetectPath;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.LostCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.profile.WalletSettingsProfilePath;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.SmartCardFlavorUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;
import rx.functions.Action1;

public class WalletSettingsPresenter extends WalletPresenter<WalletSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject TemporaryStorage temporaryStorage;

   @Nullable private FirmwareUpdateData firmwareUpdateData;

   public WalletSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      view.testSectionEnabled(SmartCardFlavorUtil.isSmartCardDevMockFlavor());
      view.testFailInstallation(temporaryStorage.failInstall());

      observeSmartCardChanges();

      observeStealthModeController(view);
      observeLockController(view);

      observeFailInstallation(view);
      observeConnectionController(view);
      observeFirmwareUpdates();
   }

   private void observeFirmwareUpdates() {
      firmwareInteractor.firmwareInfoPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               firmwareUpdateData = command.getResult();
               toggleFirmwareBargeOrVersion(firmwareUpdateData.updateAvailable());
            });
   }

   private void toggleFirmwareBargeOrVersion(boolean updateAvailable) {
      if (updateAvailable) {
         getView().firmwareUpdateCount(1);
         getView().showFirmwareBadge();
      } else {
         getView().showFirmwareVersion();
      }
   }

   private void observeSmartCardChanges() {
      Observable.combineLatest(
            smartCardInteractor.smartCardFirmwarePipe().observeSuccessWithReplay(),
            smartCardInteractor.deviceStatePipe().observeSuccessWithReplay(),
            Pair::new)
            .throttleLast(200, TimeUnit.MILLISECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(pair -> bindSmartCard(pair.first.getResult(), pair.second.getResult()));

      smartCardInteractor.stealthModePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetStealthModeCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> stealthModeChangedMessage(action.stealthModeEnabled))
                  .onFail(ErrorHandler.create(getContext(),
                        command -> stealthModeFailed()))
                  .wrap()
            );

      smartCardInteractor.lockPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                  .onFail(ErrorHandler.<SetLockStateCommand>builder(getContext())
                        .defaultMessage(R.string.wallet_smartcard_connection_error)
                        .defaultAction(a -> lockStatusFailed())
                        .build()
                  )
                  .wrap());
   }

   private void stealthModeFailed() {
      smartCardInteractor.deviceStatePipe().createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> getView().stealthModeStatus(command.getResult().stealthMode())));
   }

   private void lockStatusFailed() {
      smartCardInteractor.deviceStatePipe().createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> getView().lockStatus(command.getResult().lock())));
   }

   private void observeStealthModeController(Screen view) {
      view.stealthModeStatus()
            .compose(bindView())
            .subscribe(this::stealthModeChanged);
   }

   private void observeLockController(Screen view) {
      view.lockStatus()
            .compose(bindView())
            .subscribe(this::lockStatusChanged);
   }

   private void observeConnectionController(Screen view) {
      view.testConnection()
            .compose(bindView())
            .subscribe(this::manageConnection);
   }


   private void observeFailInstallation(Screen view) {
      view.testFailInstallation()
            .compose(bindView())
            .subscribe(this::changeFailInstallation);
   }

   private void manageConnection(boolean connected) {
      // this method for mock device
      // so, next commands will be completed
      // mock devise ignores card id
      if (connected) {
         smartCardInteractor.connectActionPipe()
               .send(new ConnectSmartCardCommand("0", false));
      } else {
         smartCardInteractor.disconnectPipe().send(new DisconnectAction());
      }
   }

   private void bindSmartCard(SmartCardFirmware smartCardFirmware, SmartCardStatus status) {
      Screen view = getView();
      //noinspection all
      view.smartCardGeneralStatus(smartCardFirmware, status.batteryLevel(), null);
      view.testConnection(status.connectionStatus().isConnected());
      view.lockStatus(status.lock());

      view.stealthModeStatus(status.stealthMode());
      view.disableDefaultPaymentValue(status.disableCardDelay());
      view.autoClearSmartCardValue(status.clearFlyeDelay());
      view.firmwareVersion(smartCardFirmware);
      toggleFirmwareBargeOrVersion(firmwareUpdateData != null && firmwareUpdateData.updateAvailable());
   }

   public void goBack() {
      navigator.goBack();
   }

   void resetPin() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            navigator.go(new WizardPinSetupPath(Action.RESET));
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void disableDefaultCardTimer() {
      navigator.go(new WalletDisableDefaultCardPath());
   }

   void autoClearSmartCardClick() {
      navigator.go(new WalletAutoClearCardsPath());
   }

   private void stealthModeChanged(boolean isEnabled) {
      smartCardInteractor.stealthModePipe().send(new SetStealthModeCommand(isEnabled));
   }

   // for test and demo
   private void changeFailInstallation(boolean failInstallation) {
      temporaryStorage.failInstall(failInstallation);
   }

   private void lockStatusChanged(boolean lock) {
      smartCardInteractor.lockPipe().send(new SetLockStateCommand(lock));
   }

   private String stealthModeChangedMessage(boolean isEnabled) {
      return getContext().getString(
            isEnabled ? R.string.wallet_card_settings_stealth_mode_on : R.string.wallet_card_settings_stealth_mode_off
      );
   }

   void smartCardProfileClick() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            navigator.go(new WalletSettingsProfilePath());
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void firmwareUpdatesClick() {
      if (firmwareUpdateData != null && firmwareUpdateData.updateAvailable()) {
         navigator.go(new StartFirmwareInstallPath());
      } else {
         navigator.go(new WalletUpToDateFirmwarePath());
      }
   }

   void factoryResetClick() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            navigator.go(new FactoryResetPath());
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   private void fetchConnectionStatus(Action1<ConnectionStatus> action) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> action.call(command.getResult().connectionStatus()))
            );
   }

   void restartSmartCard() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getView().showConfirmRestartSCDialog();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void confirmRestartSmartCard() {
      smartCardInteractor.restartSmartCardCommandActionPipe()
            .send(new RestartSmartCardCommand());
   }

   void openLostCardScreen() {
      navigator.go(new LostCardPath());
   }

   void openAboutScreen() {
      navigator.go(new AboutPath());
   }

   void goToAddNewSmartCard() {
      navigator.go(new ExistingCardDetectPath());
   }

   public interface Screen extends WalletScreen {

      void smartCardGeneralStatus(@Nullable SmartCardFirmware version, int batteryLevel, Date lastSync);

      void stealthModeStatus(boolean isEnabled);

      void lockStatus(boolean lock);

      void testConnection(boolean connected);

      void disableDefaultPaymentValue(long minutes);

      void autoClearSmartCardValue(long minutes);

      void firmwareUpdateCount(int count);

      void firmwareVersion(@Nullable SmartCardFirmware version);

      void testFailInstallation(boolean failInstall);

      Observable<Boolean> offlineMode();

      Observable<Boolean> lockStatus();

      Observable<Boolean> stealthModeStatus();

      Observable<Boolean> alertConnection();

      Observable<Boolean> testConnection();

      Observable<Boolean> testFailInstallation();

      void showFirmwareVersion();

      void showFirmwareBadge();

      void testSectionEnabled(boolean enabled);

      void showSCNonConnectionDialog();

      void showConfirmRestartSCDialog();
   }
}
