package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.InstallingUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.RetryInstallUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.SCFirmwareFacade;
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
import timber.log.Timber;

public class WalletInstallFirmwarePresenter extends WalletPresenter<WalletInstallFirmwarePresenter.Screen, WalletInstallFirmwareState> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SCFirmwareFacade firmwareFacade;
   @Inject Navigator navigator;

   public WalletInstallFirmwarePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      sendAnalyticEvent();

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

   private void sendAnalyticEvent() {
      firmwareFacade.takeFirmwareInfo()
            .compose(bindView())
            .subscribe(firmwareUpdateData -> analyticsInteractor.walletAnalyticsCommandPipe()
                        .send(new WalletAnalyticsCommand(new InstallingUpdateAction(firmwareUpdateData.smartCardId()))),
                  throwable -> Timber.e(throwable, ""));
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
      firmwareFacade.takeFirmwareInfo()
            .compose(bindView())
            .subscribe(firmwareUpdateData -> executeCombinedDataAndSendAnalytics(firmwareUpdateData, retry),
                  throwable -> Timber.e(throwable, ""));
   }

   private void executeCombinedDataAndSendAnalytics(FirmwareUpdateData firmwareUpdateData, boolean retry) {
      final RetryInstallUpdateAction retryInstallUpdateAction = new RetryInstallUpdateAction(
            firmwareUpdateData.smartCardId(),
            firmwareUpdateData.currentFirmwareVersion().firmwareVersion(),
            firmwareUpdateData.firmwareInfo().firmwareVersion(),
            retry);

      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(retryInstallUpdateAction));
   }

   public interface Screen extends WalletScreen, OperationScreen {

      void showInstallingStatus(int currentStep, int totalSteps, int progress);

   }
}
