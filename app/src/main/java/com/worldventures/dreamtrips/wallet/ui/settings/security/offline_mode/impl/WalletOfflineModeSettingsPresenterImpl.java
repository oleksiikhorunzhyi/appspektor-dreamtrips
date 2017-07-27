package com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.SettingsOfflineModeScreenAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SettingsOfflineModeStateChangeAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsScreen;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class WalletOfflineModeSettingsPresenterImpl extends WalletPresenterImpl<WalletOfflineModeSettingsScreen> implements WalletOfflineModeSettingsPresenter{

   private final AnalyticsInteractor analyticsInteractor;

   private boolean waitingForNetwork = false;

   public WalletOfflineModeSettingsPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletOfflineModeSettingsScreen view) {
      super.attachView(view);
      trackScreen();

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
      getSmartCardInteractor().offlineModeStatusPipe().send(OfflineModeStatusCommand.fetch());
   }

   @Override
   public void switchOfflineMode() {
      getSmartCardInteractor().switchOfflineModePipe().send(new SwitchOfflineModeCommand());
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
      getSmartCardInteractor().offlineModeStatusPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(isOfflineModeEnabled -> getView().setOfflineModeState(isOfflineModeEnabled));

      getSmartCardInteractor().switchOfflineModePipe()
            .observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onProgress(command -> waitingForNetwork = false)
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
            .compose(bindView())
            .subscribe(this::onOfflineModeSwitcherChanged);
   }

   private void observeNetworkState() {
      getNetworkService().observeConnectedState()
            .compose(bindView())
            .filter(networkAvailable -> networkAvailable && waitingForNetwork)
            .subscribe(networkAvailable -> switchOfflineMode());
   }

   private void onOfflineModeSwitcherChanged(boolean enabled) {
      getView().showConfirmationDialog(enabled);
   }

   private void trackStateChange(boolean isOfflineModeEnabled) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new SettingsOfflineModeStateChangeAction(isOfflineModeEnabled)));
   }

   private void trackScreen() {
      getSmartCardInteractor().offlineModeStatusPipe()
            .observeSuccess()
            .take(1)
            .map(Command::getResult)
            .subscribe(isOfflineModeEnabled -> analyticsInteractor.walletAnalyticsCommandPipe()
                  .send(new WalletAnalyticsCommand(new SettingsOfflineModeScreenAction(isOfflineModeEnabled))));
   }
}
