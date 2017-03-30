package com.worldventures.dreamtrips.wallet.ui.settings.security;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardLockAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardUnlockAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.StealthModeAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action1;

public class WalletSecuritySettingsPresenter extends WalletPresenter<WalletSecuritySettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WalletSecuritySettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      observeSmartCardChanges();

      observeStealthModeController(view);
      observeLockController(view);
   }

   private void observeSmartCardChanges() {
      smartCardInteractor.deviceStatePipe().observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(this::bindSmartCard);

      smartCardInteractor.stealthModePipe()
            .observe()
            .throttleLast(1, TimeUnit.SECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetStealthModeCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> {
                     trackSmartCardStealthMode(action.stealthModeEnabled);
                     stealthModeChangedMessage(action.stealthModeEnabled);
                  })
                  .onFail(ErrorHandler.create(getContext(),
                        command -> stealthModeFailed()))
                  .wrap()
            );

      smartCardInteractor.lockPipe()
            .observe()
            .throttleLast(1, TimeUnit.SECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> trackSmartCardLock(action.isLock()))
                  .onFail(ErrorHandler.<SetLockStateCommand>builder(getContext())
                        .defaultMessage(R.string.wallet_smartcard_connection_error)
                        .defaultAction(a -> lockStatusFailed())
                        .build()
                  )
                  .wrap());
   }

   private void trackSmartCardStealthMode(boolean stealthModeEnabled) {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new StealthModeAction(stealthModeEnabled));
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   private void trackSmartCardLock(boolean lock) {
      final WalletAnalyticsAction smartCardLockAction = lock
            ? new SmartCardLockAction()
            : new SmartCardUnlockAction();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(smartCardLockAction));
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

   private void bindSmartCard(SmartCardStatus status) {
      Screen view = getView();
      //noinspection all
      view.lockStatus(status.lock());

      view.stealthModeStatus(status.stealthMode());
      view.disableDefaultPaymentValue(status.disableCardDelay());
      view.autoClearSmartCardValue(status.clearFlyeDelay());
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

   private void lockStatusChanged(boolean lock) {
      smartCardInteractor.lockPipe().send(new SetLockStateCommand(lock));
   }

   private String stealthModeChangedMessage(boolean isEnabled) {
      return getContext().getString(
            isEnabled ? R.string.wallet_card_settings_stealth_mode_on : R.string.wallet_card_settings_stealth_mode_off
      );
   }

   private void fetchConnectionStatus(Action1<ConnectionStatus> action) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> action.call(command.getResult().connectionStatus()))
            );
   }


   void openLostCardScreen() {
      navigator.go(new LostCardPath());
   }

   void openOfflineModeScreen() {
      navigator.go(new WalletOfflineModeSettingsPath());
   }

   public interface Screen extends WalletScreen {

      void stealthModeStatus(boolean isEnabled);

      void lockStatus(boolean lock);

      void disableDefaultPaymentValue(long minutes);

      void autoClearSmartCardValue(long minutes);

      Observable<Boolean> lockStatus();

      Observable<Boolean> stealthModeStatus();

      void showSCNonConnectionDialog();

   }
}
