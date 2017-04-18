package com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class WalletOfflineModeSettingsPresenter extends WalletPresenter<WalletOfflineModeSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletNetworkService networkService;

   private boolean waitingForNetwork = false;

   WalletOfflineModeSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      observeOfflineModeState();
      observeOfflineModeSwitcher();

      observeNetworkState();
   }

   @Override
   public void onVisibilityChanged(int visibility) {
      super.onVisibilityChanged(visibility);
      if (visibility == View.VISIBLE) fetchOfflineModeState();
   }

   public void goBack() {
      navigator.goBack();
   }

   void fetchOfflineModeState() {
      smartCardInteractor.offlineModeStatusPipe().send(OfflineModeStatusCommand.fetch());
   }

   void switchOfflineMode() {
      smartCardInteractor.switchOfflineModePipe().send(new SwitchOfflineModeCommand());
   }

   void switchOfflineModeCanceled() {
      waitingForNetwork = false;
      fetchOfflineModeState();
   }

   void navigateToSystemSettings() {
      getContext().startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
   }

   private void observeOfflineModeState() {
      smartCardInteractor.offlineModeStatusPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(isOfflineModeEnabled -> getView().setOfflineModeState(isOfflineModeEnabled));

      smartCardInteractor.switchOfflineModePipe()
            .observe()
            .throttleLast(1, TimeUnit.SECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onProgress(command -> waitingForNetwork = false)
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
      networkService.observeConnectedState()
            .compose(bindView())
            .filter(networkAvailable -> networkAvailable && waitingForNetwork)
            .subscribe(networkAvailable -> switchOfflineMode());
   }

   private void onOfflineModeSwitcherChanged(boolean enabled) {
      getView().showConfirmationDialog(enabled);
   }

   public interface Screen extends WalletScreen {

      Observable<Boolean> observeOfflineModeSwitcher();

      OperationView<SwitchOfflineModeCommand> provideOperationView();

      void showConfirmationDialog(boolean enable);

      void setOfflineModeState(boolean enabled);
   }

}
