package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.command.firmware.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess.WalletSuccessInstallFirmwarePath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;

public class WalletInstallFirmwarePresenter extends WalletPresenter<WalletInstallFirmwarePresenter.Screen, WalletInstallFirmwareState> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject Navigator navigator;

   private final FirmwareUpdateData firmwareData;

   public WalletInstallFirmwarePresenter(Context context, Injector injector, FirmwareUpdateData firmwareData) {
      super(context, injector);
      this.firmwareData = firmwareData;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      firmwareInteractor.installFirmwarePipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper(firmwareInteractor.installFirmwarePipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<InstallFirmwareCommand>forView(getView())
                  .onSuccess(command -> openSuccess())
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap().
                        onStart(installFirmwareCommand -> state.started = true)
            );
      if (!state.started) {
         install();
      }
   }

   @Override
   public void onNewViewState() {
      state = new WalletInstallFirmwareState();
   }

   protected void install() {
      getView().showProgress(null);
      firmwareInteractor.installFirmwarePipe().send(new InstallFirmwareCommand(firmwareData));
   }

   private void openSuccess() {
      History.Builder historyBuilder = History.emptyBuilder();
      historyBuilder.push(new CardListPath());
      historyBuilder.push(new WalletSuccessInstallFirmwarePath());
      navigator.setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
   }

   void goToDashboard() {
      navigator.single(new CardListPath(), Flow.Direction.BACKWARD);
   }

   void retry() {
      install();
   }

   public interface Screen extends WalletScreen, OperationScreen {

   }
}