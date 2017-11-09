package com.worldventures.wallet.ui.settings.security.impl;


import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.SmartCardLockAction;
import com.worldventures.wallet.analytics.settings.SmartCardUnlockAction;
import com.worldventures.wallet.analytics.settings.StealthModeAction;
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.domain.entity.SmartCardStatus;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.SetLockStateCommand;
import com.worldventures.wallet.service.command.SetPinEnabledCommand;
import com.worldventures.wallet.service.command.SetStealthModeCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsScreen;
import com.worldventures.wallet.ui.wizard.pin.Action;
import com.worldventures.wallet.util.WalletFeatureHelper;

import java.util.concurrent.TimeUnit;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.event.PinStatusEvent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WalletSecuritySettingsPresenterImpl extends WalletPresenterImpl<WalletSecuritySettingsScreen> implements WalletSecuritySettingsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WalletFeatureHelper featureHelper;

   public WalletSecuritySettingsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         WalletFeatureHelper walletFeatureHelper) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.featureHelper = walletFeatureHelper;
   }

   @Override
   public void attachView(WalletSecuritySettingsScreen view) {
      super.attachView(view);
      featureHelper.prepareSettingsSecurityScreen(getView());
      observeSmartCardChanges();

      observeStealthModeController(getView());
      observeLockController(getView());
      smartCardInteractor.checkPinStatusActionPipe().send(new CheckPinStatusAction());
   }

   private void observeSmartCardChanges() {
      smartCardInteractor.deviceStatePipe().observeSuccessWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Command::getResult)
            .subscribe(this::bindSmartCard);

      smartCardInteractor.stealthModePipe()
            .observe()
            .throttleLast(1, TimeUnit.SECONDS)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetStealthMode())
                  .onSuccess(action -> trackSmartCardStealthMode(action.stealthModeEnabled))
                  .onFail((setStealthModeCommand, throwable) -> stealthModeFailed())
                  .create()
            );

      smartCardInteractor.lockPipe()
            .observe()
            .throttleLast(1, TimeUnit.SECONDS)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSeLockState())
                  .onSuccess(action -> trackSmartCardLock(action.isLock()))
                  .create());

      Observable.merge(
            smartCardInteractor.pinStatusEventPipe()
                  .observeSuccess()
                  .map(pinStatusEvent -> pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.DISABLED),
            smartCardInteractor.setPinEnabledCommandActionPipe()
                  .observeSuccess()
                  .map(Command::getResult))
            .startWith(true)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::applyPinStatus);
   }

   @Override
   public void addPin() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getNavigator().goEnterPinSettings(Action.ADD);
         } else {
            //noinspection ConstantConditions
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void removePin() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            smartCardInteractor.setPinEnabledCommandActionPipe().send(new SetPinEnabledCommand(false));
         } else {
            //noinspection ConstantConditions
            getView().showSCNonConnectionDialog();
         }
      });
   }

   private void applyPinStatus(boolean isEnabled) {
      //noinspection ConstantConditions
      getView().setLockToggleEnable(isEnabled);
      getView().setAddRemovePinState(isEnabled);
   }

   private void trackSmartCardStealthMode(boolean stealthModeEnabled) {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new StealthModeAction(stealthModeEnabled));
      analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand);
   }

   private void trackSmartCardLock(boolean lock) {
      final WalletAnalyticsAction smartCardLockAction = lock
            ? new SmartCardLockAction()
            : new SmartCardUnlockAction();
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(smartCardLockAction));
   }

   private void stealthModeFailed() {
      //noinspection ConstantConditions
      smartCardInteractor.deviceStatePipe().createObservable(DeviceStateCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> getView().stealthModeStatus(command.getResult().stealthMode())));
   }

   @Override
   public void lockStatusFailed() {
      //noinspection ConstantConditions
      smartCardInteractor.deviceStatePipe().createObservable(DeviceStateCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> getView().lockStatus(command.getResult().lock())));
   }

   private void observeStealthModeController(WalletSecuritySettingsScreen view) {
      view.stealthModeStatus()
            .compose(getView().bindUntilDetach())
            .subscribe(this::stealthModeChanged);
   }

   private void observeLockController(WalletSecuritySettingsScreen view) {
      view.lockStatus()
            .compose(getView().bindUntilDetach())
            .subscribe(this::lockStatusChanged);
   }

   private void bindSmartCard(SmartCardStatus status) {
      WalletSecuritySettingsScreen view = getView();
      //noinspection all
      view.lockStatus(status.lock());

      view.stealthModeStatus(status.stealthMode());
      view.disableDefaultPaymentValue(status.disableCardDelay());
      view.autoClearSmartCardValue(status.clearFlyeDelay());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void resetPin() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getNavigator().goEnterPinSettings(Action.RESET);
         } else {
            //noinspection ConstantConditions
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void disableDefaultCardTimer() {
      getNavigator().goWalletDisableDefault();
   }

   @Override
   public void autoClearSmartCardClick() {
      getNavigator().goWalletAutoClear();
   }

   private void stealthModeChanged(boolean isEnabled) {
      smartCardInteractor.stealthModePipe().send(new SetStealthModeCommand(isEnabled));
   }

   private void lockStatusChanged(boolean lock) {
      smartCardInteractor.lockPipe().send(new SetLockStateCommand(lock));
   }

   private void fetchConnectionStatus(Action1<ConnectionStatus> action) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> action.call(command.getResult().connectionStatus()))
            );
   }


   @Override
   public void openLostCardScreen() {
      featureHelper.openFindCard(getView().getViewContext(), () -> getNavigator().goLostCard());
   }

   @Override
   public void openOfflineModeScreen() {
      getNavigator().goSettingsOfflineMode();
   }
}
