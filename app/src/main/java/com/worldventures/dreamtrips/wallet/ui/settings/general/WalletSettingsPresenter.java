package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FactoryResetManager;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate.WalletUpToDateFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPath;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;

public class WalletSettingsPresenter extends WalletPresenter<WalletSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject TemporaryStorage temporaryStorage;
   @Inject FactoryResetManager factoryResetManager;

   private SmartCard smartCard;
   @Nullable private Firmware firmware;


   public WalletSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
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
               firmware = command.getResult();
               toggleFirmwareBargeOrVersion(firmware.updateAvailable());
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
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindSmartCard(this.smartCard = command.getResult()));

      smartCardInteractor.stealthModePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetStealthModeCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> stealthModeChangedMessage(action.stealthModeEnabled))
                  .onFail(ErrorHandler.create(getContext(),
                        command -> getView().stealthModeStatus(smartCard.stealthMode())))
                  .wrap()
            );

      smartCardInteractor.lockPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(setLockStateCommand -> {
                  })
                  .onFail(ErrorHandler.<SetLockStateCommand>builder(getContext())
                        .handle(IllegalArgumentException.class, R.string.wallet_dashboard_unlock_error)
                        .defaultAction(a -> getView().lockStatus(smartCard.lock()))
                        .build()
                  )
                  .wrap());
   }

   private void observeStealthModeController(Screen view) {
      view.stealthModeStatus()
            .compose(bindView())
            .skip(1)
            .filter(checkedFlag -> smartCard.stealthMode() != checkedFlag)
            .subscribe(this::stealthModeChanged);
   }

   private void observeLockController(Screen view) {
      view.lockStatus()
            .compose(bindView())
            .skip(1)
            .filter(lock -> smartCard.lock() != lock)
            .subscribe(this::lockStatusChanged);
   }

   private void observeConnectionController(Screen view) {
      view.testConnection()
            .compose(bindView())
            .skip(1)
            .filter(connected -> (smartCard.connectionStatus().isConnected()) != connected)
            .subscribe(this::manageConnection);
   }


   private void observeFailInstallation(Screen view) {
      view.testFailInstallation()
            .compose(bindView())
            .skip(1)
            .filter(compatible -> temporaryStorage.failInstall() != compatible)
            .subscribe(this::changeFailInstallation);
   }

   private void manageConnection(boolean connected) {
      if (connected) {
         smartCardInteractor.connectActionPipe()
               .createObservable(new ConnectSmartCardCommand(smartCard))
               .compose(bindViewIoToMainComposer())
               .subscribe(OperationActionStateSubscriberWrapper.<ConnectSmartCardCommand>forView(getView().provideOperationDelegate())
                     .onFail(ErrorHandler.create(getContext(),
                           action -> getView().testConnection(smartCard.connectionStatus().isConnected()))
                     )
                     .wrap()
               );
      } else {
         smartCardInteractor.disconnectPipe()
               .createObservable(new DisconnectAction())
               .compose(bindViewIoToMainComposer())
               .subscribe(OperationActionStateSubscriberWrapper.<DisconnectAction>forView(getView().provideOperationDelegate())
                     .onFail(ErrorHandler.create(getContext(),
                           action -> getView().testConnection(smartCard.connectionStatus().isConnected()))
                     )
                     .wrap()
               );
      }
   }

   private void bindSmartCard(SmartCard smartCard) {
      Screen view = getView();
      //noinspection all
      view.testConnection(smartCard.connectionStatus().isConnected());
      view.stealthModeStatus(smartCard.stealthMode());
      view.lockStatus(smartCard.lock());
      view.disableDefaultPaymentValue(smartCard.disableCardDelay());
      view.autoClearSmartCardValue(smartCard.clearFlyeDelay());
      view.firmwareVersion(smartCard.firmWareVersion());
      toggleFirmwareBargeOrVersion(firmware != null && firmware.updateAvailable());
   }

   public void goBack() {
      navigator.goBack();
   }

   void resetPin() {
      navigator.go(new WizardPinSetupPath(smartCard, Action.RESET));
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

   void firmwareUpdatesClick() {
      if (firmware != null && firmware.updateAvailable()) {
         navigator.go(new WalletNewFirmwareAvailablePath());
      } else {
         navigator.go(new WalletUpToDateFirmwarePath());
      }
   }

   void factoryResetClick() {
      getView().showConfirmFactoryResetDialog();
   }

   void executeFactoryReset() {
      factoryResetManager.observeFactoryResetPipe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<ResetSmartCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> navigator.finish())
                  .onFail(ErrorHandler.<ResetSmartCardCommand>builder(getContext())
                        .defaultMessage(R.string.wallet_wizard_setup_error)
                        .build())
                  .wrap());

      factoryResetManager.factoryReset();
   }

   public interface Screen extends WalletScreen {

      void stealthModeStatus(boolean isEnabled);

      void lockStatus(boolean lock);

      void testConnection(boolean connected);

      void disableDefaultPaymentValue(long millis);

      void autoClearSmartCardValue(long millis);

      void firmwareUpdateCount(int count);

      void firmwareVersion(String version);

      void testFailInstallation(boolean failInstall);

      Observable<Boolean> stealthModeStatus();

      Observable<Boolean> lockStatus();

      Observable<Boolean> testConnection();

      Observable<Boolean> testFailInstallation();

      void showFirmwareVersion();

      void showFirmwareBadge();

      void showConfirmFactoryResetDialog();
   }
}
