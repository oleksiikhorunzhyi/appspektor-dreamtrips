package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.os.Parcelable;
import android.widget.Toast;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate.WalletUpToDateFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.ThrowableHelper;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;

public class WalletSettingsPresenter extends WalletPresenter<WalletSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject ThrowableHelper throwableHelper;

   private SmartCard smartCard;
   private FirmwareInfo firmware;


   public WalletSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeSmartCardChanges();

      observeStealthModeController(view);
      observeLockController(view);
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
               getView().firmwareUpdateCount(firmware.byteSize() > 0 ? 1 : 0);
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
                        .onSuccess(action -> stealthModeChangedMessage(action.stealthModeEnabled), action -> {})
                        .onFail(throwableHelper.provideMessageHolder(
                              command -> getView().stealthModeStatus(smartCard.stealthMode()))
                        )
                        .wrap()
            );

      smartCardInteractor.lockPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(setLockStateCommand -> {})
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

   }

   public void goBack() {
      navigator.goBack();
   }

   public void resetPin() {
      navigator.go(new WizardPinSetupPath(smartCard, WizardPinSetupPath.Action.RESET));
   }

   public void disableDefaultCardTimer() {
      navigator.go(new WalletDisableDefaultCardPath());
   }

   public void autoClearSmartCardClick() {
      navigator.go(new WalletAutoClearCardsPath());
   }

   private void stealthModeChanged(boolean isEnabled) {
      smartCardInteractor.stealthModePipe().send(new SetStealthModeCommand(isEnabled));
   }

   private void lockStatusChanged(boolean lock) {
      smartCardInteractor.lockPipe().send(new SetLockStateCommand(lock));
   }

   private String stealthModeChangedMessage(boolean isEnabled) {
      return getContext().getString(
            isEnabled ? R.string.wallet_card_settings_stealth_mode_on : R.string.wallet_card_settings_stealth_mode_off
      );
   }

   public void firmwareUpdatesClick() {
      // TODO: 9/21/16 firmware.byteSize() > 0 is a temp criteria
      if (firmware != null && firmware.byteSize() > 0) {
         // // TODO: 9/21/16 open update screen
         Toast.makeText(getContext(), "Need implement", Toast.LENGTH_LONG).show();
      } else {
         navigator.go(new WalletUpToDateFirmwarePath());
      }
   }

   public interface Screen extends WalletScreen {

      void stealthModeStatus(boolean isEnabled);

      void lockStatus(boolean lock);

      void testConnection(boolean connected);

      void disableDefaultPaymentValue(long millis);

      void autoClearSmartCardValue(long millis);

      void firmwareUpdateCount(int count);

      Observable<Boolean> stealthModeStatus();

      Observable<Boolean> lockStatus();

      Observable<Boolean> testConnection();
   }
}
