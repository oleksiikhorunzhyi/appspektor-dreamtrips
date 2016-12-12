package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardManager;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset.FactoryResetPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate.WalletUpToDateFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.profile.WalletSettingsProfilePath;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.SmartCardFlavorUtil;

import java.util.Date;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;

public class WalletSettingsPresenter extends WalletPresenter<WalletSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject TemporaryStorage temporaryStorage;
   @Inject SmartCardManager smartCardManager;

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
      smartCardManager.smartCardObservable()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::bindSmartCard);

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
                  .onSuccess(setLockStateCommand -> {
                  })
                  .onFail(ErrorHandler.<SetLockStateCommand>builder(getContext())
                        .defaultMessage(R.string.wallet_smartcard_connection_error)
                        .defaultAction(a -> lockStatusFailed())
                        .build()
                  )
                  .wrap());
   }

   private void stealthModeFailed() {
      smartCardManager.singleSmartCardObservable()
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCard -> getView().stealthModeStatus(smartCard.stealthMode()));
   }

   private void lockStatusFailed() {
      smartCardManager.singleSmartCardObservable()
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCard -> getView().lockStatus(smartCard.lock()));
   }

   private void observeStealthModeController(Screen view) {
      view.stealthModeStatus()
            .compose(bindView())
            .skip(1)
            .flatMap(stealthMode -> smartCardManager.singleSmartCardObservable()
                  .filter(smartCard -> smartCard.stealthMode() != stealthMode)
                  .map(smartCard -> stealthMode)
            )
            .subscribe(this::stealthModeChanged);
   }

   private void observeLockController(Screen view) {
      view.lockStatus()
            .compose(bindView())
            .skip(1)
            .flatMap(lockStatus -> smartCardManager.singleSmartCardObservable()
                  .filter(smartCard -> smartCard.lock() != lockStatus)
                  .map(smartCard -> lockStatus)
            )
            .subscribe(this::lockStatusChanged);
   }

   private void observeConnectionController(Screen view) {
      view.testConnection()
            .compose(bindView())
            .skip(1)
            .flatMap(connectedValue -> smartCardManager.singleSmartCardObservable()
                  .filter(smartCard -> (smartCard.connectionStatus().isConnected()) != connectedValue)
                  .map(smartCard -> new Pair<>(smartCard, connectedValue))
            )
            .subscribe(pair -> manageConnection(pair.first, pair.second));
   }


   private void observeFailInstallation(Screen view) {
      view.testFailInstallation()
            .compose(bindView())
            .skip(1)
            .filter(compatible -> temporaryStorage.failInstall() != compatible)
            .subscribe(this::changeFailInstallation);
   }

   private void manageConnection(SmartCard smartCard, boolean connected) {
      if (connected) {
         smartCardInteractor.connectActionPipe()
               .createObservable(new ConnectSmartCardCommand(smartCard, false))
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
      view.smartCardGeneralStatus(smartCard.firmWareVersion(), smartCard.batteryLevel(), null);
      view.testConnection(smartCard.connectionStatus().isConnected());
      view.stealthModeStatus(smartCard.stealthMode());
      view.lockStatus(smartCard.lock());
      view.disableDefaultPaymentValue(smartCard.disableCardDelay());
      view.autoClearSmartCardValue(smartCard.clearFlyeDelay());
      view.firmwareVersion(smartCard.firmWareVersion());
      toggleFirmwareBargeOrVersion(firmwareUpdateData != null && firmwareUpdateData.updateAvailable());
   }

   public void goBack() {
      navigator.goBack();
   }

   void resetPin() {
      smartCardManager.singleSmartCardObservable()
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCard ->
                  navigator.go(new WizardPinSetupPath(smartCard, Action.RESET)));
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

   public void smartCardProfileClick() {
      navigator.go(new WalletSettingsProfilePath());
   }

   void firmwareUpdatesClick() {
      if (firmwareUpdateData != null && firmwareUpdateData.updateAvailable()) {
         navigator.go(new WalletNewFirmwareAvailablePath());
      } else {
         navigator.go(new WalletUpToDateFirmwarePath());
      }
   }

   void factoryResetClick() {
      navigator.go(new FactoryResetPath());
   }

   void confirmResetSmartCard() {
      smartCardInteractor.restartSmartCardCommandActionPipe()
            .send(new RestartSmartCardCommand());
   }

   public interface Screen extends WalletScreen {

      void smartCardGeneralStatus(String version, int batteryLevel, Date lastSync);

      void stealthModeStatus(boolean isEnabled);

      void lockStatus(boolean lock);

      void testConnection(boolean connected);

      void disableDefaultPaymentValue(long minutes);

      void autoClearSmartCardValue(long minutes);

      void firmwareUpdateCount(int count);

      void firmwareVersion(String version);

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
   }
}
