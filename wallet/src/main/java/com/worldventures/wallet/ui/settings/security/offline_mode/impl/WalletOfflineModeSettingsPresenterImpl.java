package com.worldventures.wallet.ui.settings.security.offline_mode.impl;


import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.SettingsOfflineModeScreenAction;
import com.worldventures.wallet.analytics.settings.SettingsOfflineModeStateChangeAction;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter;
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsScreen;
import com.worldventures.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.wallet.util.NetworkUnavailableException;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletOfflineModeSettingsPresenterImpl extends WalletPresenterImpl<WalletOfflineModeSettingsScreen> implements WalletOfflineModeSettingsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletNetworkDelegate networkDelegate;
   private final WalletAnalyticsInteractor analyticsInteractor;

   private boolean waitingForNetwork = false;

   public WalletOfflineModeSettingsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletNetworkDelegate networkDelegate, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.networkDelegate = networkDelegate;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletOfflineModeSettingsScreen view) {
      super.attachView(view);
      trackScreen();
      networkDelegate.setup(view);

      observeOfflineModeState();
      observeOfflineModeSwitcher();

      observeNetworkState();
      fetchOfflineModeState();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void fetchOfflineModeState() {
      smartCardInteractor.offlineModeStatusPipe().send(OfflineModeStatusCommand.fetch());
   }

   @Override
   public void switchOfflineMode() {
      smartCardInteractor.switchOfflineModePipe().send(new SwitchOfflineModeCommand());
   }

   @Override
   public void switchOfflineModeCanceled() {
      waitingForNetwork = false;
      fetchOfflineModeState();
   }

   @Override
   public void navigateToSystemSettings() {
      getNavigator().goSystemSettings();
   }

   private void observeOfflineModeState() {
      smartCardInteractor.offlineModeStatusPipe()
            .observeSuccess()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Command::getResult)
            .subscribe(isOfflineModeEnabled -> getView().setOfflineModeState(isOfflineModeEnabled));

      smartCardInteractor.switchOfflineModePipe()
            .observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onProgress((command, progress) -> waitingForNetwork = false)
                  .onSuccess(command -> trackStateChange(command.getResult()))
                  .onFail((command, throwable) -> {
                     if (throwable.getCause() instanceof NetworkUnavailableException) {
                        waitingForNetwork = true;
                     }
                  })
                  .create()
            );
   }

   private void observeOfflineModeSwitcher() {
      getView().observeOfflineModeSwitcher()
            .compose(getView().bindUntilDetach())
            .subscribe(this::onOfflineModeSwitcherChanged);
   }

   private void observeNetworkState() {
      networkDelegate.observeConnectedState()
            .compose(getView().bindUntilDetach())
            .filter(networkAvailable -> networkAvailable && waitingForNetwork)
            .subscribe(networkAvailable -> switchOfflineMode());
   }

   private void onOfflineModeSwitcherChanged(boolean enabled) {
      getView().showConfirmationDialog(enabled);
   }

   private void trackStateChange(boolean isOfflineModeEnabled) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new SettingsOfflineModeStateChangeAction(isOfflineModeEnabled)));
   }

   private void trackScreen() {
      smartCardInteractor.offlineModeStatusPipe()
            .observeSuccess()
            .take(1)
            .map(Command::getResult)
            .subscribe(isOfflineModeEnabled -> analyticsInteractor.walletAnalyticsPipe()
                  .send(new WalletAnalyticsCommand(new SettingsOfflineModeScreenAction(isOfflineModeEnabled))));
   }
}
