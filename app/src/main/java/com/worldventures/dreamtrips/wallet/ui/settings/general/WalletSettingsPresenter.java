package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset.FactoryResetPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate.WalletUpToDateFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.ThrowableHelper;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath.Action.RESET;

public class WalletSettingsPresenter extends WalletPresenter<WalletSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject ThrowableHelper throwableHelper;
   @Inject TemporaryStorage temporaryStorage;

   private SmartCard smartCard;
   private FirmwareInfo firmware;


   public WalletSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      view.testNewFirmwareAvailable(temporaryStorage.newFirmwareIsAvailable());
      view.testFirmwareIsCompatible(temporaryStorage.firmwareIsCompatible());
      view.testEnoughSpaceForFirmware(temporaryStorage.enoughSpaceForFirmware());

      observeSmartCardChanges();

      observeStealthModeController(view);
      observeLockController(view);
      observeFirmwareAvailableController(view);
      observeFirmwareCompatibleController(view);

      observeEnoughSpace(view);
      observeConnectionController(view);
      observeFirmwareUpdates();
   }

   private void observeFirmwareUpdates() {
      firmwareInteractor.firmwareInfoPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               firmware = command.getResult();
               // this solution is not like iOS. After server was deploy, update this criteria
               boolean newFirmwareAvailable = firmware.byteSize() > 0;
               if (newFirmwareAvailable) {
                  getView().firmwareUpdateCount(1);
                  getView().showFirmwareBadge();
               } else {
                  getView().showFirmwareVersion();
               }
            });
   }

   private void observeSmartCardChanges() {
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindSmartCard(this.smartCard = command.getResult()));

      smartCardInteractor.stealthModePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(
                  OperationSubscriberWrapper.<SetStealthModeCommand>forView(getView().provideOperationDelegate())
                        .onSuccess(action -> stealthModeChangedMessage(action.stealthModeEnabled))
                        .onFail(throwableHelper.provideMessageHolder(
                              command -> getView().stealthModeStatus(smartCard.stealthMode()))
                        )
                        .wrap()
            );

      smartCardInteractor.lockPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(setLockStateCommand -> {
                  })
                  .onFail(getContext().getString(R.string.wallet_dashboard_unlock_error), a -> getView().lockStatus(smartCard
                        .lock()))
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
            .filter(connected -> (smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED) != connected)
            .subscribe(this::manageConnection);
   }

   private void observeFirmwareAvailableController(Screen view) {
      view.testNewFirmwareAvailable()
            .compose(bindView())
            .skip(1)
            .filter(available -> temporaryStorage.newFirmwareIsAvailable() != available)
            .subscribe(this::changeFirmwareUpdates);
   }

   private void observeFirmwareCompatibleController(Screen view) {
      view.testFirmwareIsCompatible()
            .compose(bindView())
            .skip(1)
            .filter(compatible -> temporaryStorage.firmwareIsCompatible() != compatible)
            .subscribe(this::changeFirmwareIsCompatible);
   }

   private void observeEnoughSpace(Screen view) {
      view.testEnoughSpaceForFirmware()
            .compose(bindView())
            .skip(1)
            .filter(compatible -> temporaryStorage.enoughSpaceForFirmware() != compatible)
            .subscribe(this::changeEnoughSpace);
   }

   private void manageConnection(boolean connected) {
      if (connected) {
         smartCardInteractor.connectActionPipe()
               .createObservable(new ConnectSmartCardCommand(smartCard))
               .compose(bindViewIoToMainComposer())
               .subscribe(OperationSubscriberWrapper.<ConnectSmartCardCommand>forView(getView().provideOperationDelegate())
                     .onFail(throwableHelper.provideMessageHolder(
                           action -> getView().testConnection(smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED))
                     )
                     .wrap()
               );
      } else {
         smartCardInteractor.disconnectPipe()
               .createObservable(new DisconnectAction())
               .compose(bindViewIoToMainComposer())
               .subscribe(OperationSubscriberWrapper.<DisconnectAction>forView(getView().provideOperationDelegate())
                     .onFail(throwableHelper.provideMessageHolder(
                           action -> getView().testConnection(smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED))
                     )
                     .wrap()
               );
      }
   }

   private void bindSmartCard(SmartCard smartCard) {
      Screen view = getView();
      //noinspection all
      view.testConnection(smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED);
      view.stealthModeStatus(smartCard.stealthMode());
      view.lockStatus(smartCard.lock());
      view.disableDefaultPaymentValue(smartCard.disableCardDelay());
      view.autoClearSmartCardValue(smartCard.clearFlyeDelay());
      view.firmwareVersion(smartCard.firmWareVersion());
      if (firmware == null || firmware.byteSize() == 0) {
         view.showFirmwareVersion();
      }
   }

   public void goBack() {
      navigator.goBack();
   }

   void resetPin() {
      navigator.go(new WizardPinSetupPath(smartCard, RESET));
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
   private void changeFirmwareUpdates(boolean available) {
      temporaryStorage.newFirmwareIsAvailable(available);
      //for ui will updated
      firmwareInteractor.firmwareInfoPipe().send(new FetchFirmwareInfoCommand());
   }

   // for test and demo
   private void changeFirmwareIsCompatible(boolean compatible) {
      temporaryStorage.firmwareIsCompatible(compatible);
      //for ui will updated
      firmwareInteractor.firmwareInfoPipe().send(new FetchFirmwareInfoCommand());
   }

   // for test and demo
   private void changeEnoughSpace(boolean enoughSpace) {
      temporaryStorage.enoughSpaceForFirmware(enoughSpace);
      //for ui will updated
      firmwareInteractor.firmwareInfoPipe().send(new FetchFirmwareInfoCommand());
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
      // TODO: 9/21/16 firmware.byteSize() > 0 is a temp criteria
      if (firmware != null && firmware.byteSize() > 0) {
         // // TODO: 9/21/16 open update screen
         navigator.go(new WalletNewFirmwareAvailablePath());
      } else {
         navigator.go(new WalletUpToDateFirmwarePath());
      }
   }

   void factoryResetClick() {
      navigator.go(new FactoryResetPath());
   }

   public interface Screen extends WalletScreen {

      void stealthModeStatus(boolean isEnabled);

      void lockStatus(boolean lock);

      void testConnection(boolean connected);

      void disableDefaultPaymentValue(long millis);

      void autoClearSmartCardValue(long millis);

      void firmwareUpdateCount(int count);

      void firmwareVersion(String version);

      void testNewFirmwareAvailable(boolean available);

      void testFirmwareIsCompatible(boolean compatible);

      void testEnoughSpaceForFirmware(boolean compatible);

      Observable<Boolean> stealthModeStatus();

      Observable<Boolean> lockStatus();

      Observable<Boolean> testConnection();

      Observable<Boolean> testNewFirmwareAvailable();

      Observable<Boolean> testFirmwareIsCompatible();

      Observable<Boolean> testEnoughSpaceForFirmware();

      void showFirmwareVersion();

      void showFirmwareBadge();
   }
}
