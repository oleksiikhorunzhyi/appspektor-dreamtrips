package com.worldventures.dreamtrips.wallet.ui.settings.security.impl;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardLockAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardUnlockAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.StealthModeAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPinEnabledCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsScreen;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import java.util.concurrent.TimeUnit;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.event.PinStatusEvent;
import rx.Observable;
import rx.functions.Action1;

public class WalletSecuritySettingsPresenterImpl extends WalletPresenterImpl<WalletSecuritySettingsScreen> implements WalletSecuritySettingsPresenter {

   private final AnalyticsInteractor analyticsInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;
   private final WalletFeatureHelper featureHelper;

   public WalletSecuritySettingsPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory,
         WalletFeatureHelper walletFeatureHelper) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
      this.featureHelper = walletFeatureHelper;
   }

   @Override
   public void attachView(WalletSecuritySettingsScreen view) {
      super.attachView(view);
      featureHelper.prepareSettingsSecurityScreen(getView());
      observeSmartCardChanges();

      observeStealthModeController(getView());
      observeLockController(getView());
      getSmartCardInteractor().checkPinStatusActionPipe().send(new CheckPinStatusAction());
   }

   private void observeSmartCardChanges() {
      getSmartCardInteractor().deviceStatePipe().observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(this::bindSmartCard);

      getSmartCardInteractor().stealthModePipe()
            .observe()
            .throttleLast(1, TimeUnit.SECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetStealthModeCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> trackSmartCardStealthMode(action.stealthModeEnabled))
                  .onFail(errorHandlerFactory.errorHandler(command -> stealthModeFailed()))
                  .wrap()
            );

      getSmartCardInteractor().lockPipe()
            .observe()
            .throttleLast(1, TimeUnit.SECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> trackSmartCardLock(action.isLock()))
                  .onFail(errorHandlerFactory.<SetLockStateCommand>builder()
                        .defaultMessage(R.string.wallet_smartcard_connection_error)
                        .defaultAction(a -> lockStatusFailed())
                        .build()
                  )
                  .wrap());

      Observable.merge(
            getSmartCardInteractor().pinStatusEventPipe()
                  .observeSuccess()
                  .map(pinStatusEvent -> pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.DISABLED),
            getSmartCardInteractor().setPinEnabledCommandActionPipe()
                  .observeSuccess()
                  .map(Command::getResult))
            .startWith(true)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::applyPinStatus);
   }

   @Override
   public void addPin() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            //TODO add Action.ADD to args
            getNavigator().goEnterPin();
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
            getSmartCardInteractor().setPinEnabledCommandActionPipe().send(new SetPinEnabledCommand(false));
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
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   private void trackSmartCardLock(boolean lock) {
      final WalletAnalyticsAction smartCardLockAction = lock
            ? new SmartCardLockAction()
            : new SmartCardUnlockAction();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(smartCardLockAction));
   }

   private void stealthModeFailed() {
      //noinspection ConstantConditions
      getSmartCardInteractor().deviceStatePipe().createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> getView().stealthModeStatus(command.getResult().stealthMode())));
   }

   private void lockStatusFailed() {
      //noinspection ConstantConditions
      getSmartCardInteractor().deviceStatePipe().createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> getView().lockStatus(command.getResult().lock())));
   }

   private void observeStealthModeController(WalletSecuritySettingsScreen view) {
      view.stealthModeStatus()
            .compose(bindView())
            .subscribe(this::stealthModeChanged);
   }

   private void observeLockController(WalletSecuritySettingsScreen view) {
      view.lockStatus()
            .compose(bindView())
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
            //TODO : add Action.RESET to args
            getNavigator().goEnterPin();
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
      getSmartCardInteractor().stealthModePipe().send(new SetStealthModeCommand(isEnabled));
   }

   private void lockStatusChanged(boolean lock) {
      getSmartCardInteractor().lockPipe().send(new SetLockStateCommand(lock));
   }

   private void fetchConnectionStatus(Action1<ConnectionStatus> action) {
      getSmartCardInteractor().deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
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
