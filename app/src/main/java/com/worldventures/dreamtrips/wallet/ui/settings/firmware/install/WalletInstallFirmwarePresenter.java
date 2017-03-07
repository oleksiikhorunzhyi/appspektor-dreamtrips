package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.InstallingUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.RetryInstallUpdateAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess.WalletSuccessInstallFirmwarePath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;

public class WalletInstallFirmwarePresenter extends WalletPresenter<WalletInstallFirmwarePresenter.Screen, WalletInstallFirmwareState> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Navigator navigator;

   public WalletInstallFirmwarePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();

      firmwareInteractor.installFirmwarePipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper(firmwareInteractor.installFirmwarePipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<InstallFirmwareCommand>forView(getView())
                  .onSuccess(command -> openSuccess(command.getResult()))
                  .onProgress((command, integer) -> getView().showInstallingStatus(command.getCurrentStep(),
                        InstallFirmwareCommand.INSTALL_FIRMWARE_TOTAL_STEPS, integer))
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap()
                  .onStart(installFirmwareCommand -> state.started = true)
            );
      if (!state.started) {
         install();
      }
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new InstallingUpdateAction()));
   }

   @Override
   public void onNewViewState() {
      state = new WalletInstallFirmwareState();
   }

   protected void install() {
      getView().showProgress(null);
      getView().showInstallingStatus(0, InstallFirmwareCommand.INSTALL_FIRMWARE_TOTAL_STEPS, 0);
      firmwareInteractor.installFirmwarePipe().send(new InstallFirmwareCommand());
   }

   private void openSuccess(FirmwareUpdateData firmwareUpdateData) {
      History.Builder historyBuilder = History.emptyBuilder();
      historyBuilder.push(new CardListPath());
      historyBuilder.push(new WalletSuccessInstallFirmwarePath(firmwareUpdateData));
      navigator.setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
   }

   void cancelReinstall() {
      sendRetryAnalyticAction(false);
      navigator.finish();
   }

   void retry() {
      install();
      sendRetryAnalyticAction(true);
   }

   private void sendRetryAnalyticAction(boolean retry) {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new RetryInstallUpdateAction(retry)));
   }

   public interface Screen extends WalletScreen, OperationScreen {

      void showInstallingStatus(int currentStep, int totalSteps, int progress);

   }
}
