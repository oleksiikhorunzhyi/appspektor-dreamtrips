package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;


import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.firmware.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess.WalletSuccessInstallFirmwarePath;

import java.io.File;

import javax.inject.Inject;

import flow.Flow;
import flow.History;

public class WalletInstallFirmwarePresenter extends WalletPresenter<WalletInstallFirmwarePresenter.Screen, Parcelable> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor interactor;
   @Inject Navigator navigator;

   private final String filePath;

   public WalletInstallFirmwarePresenter(Context context, Injector injector, String filePath) {
      super(context, injector);
      this.filePath = filePath;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      install();
   }

   protected void install() {
      getView().onStart();
      firmwareInteractor.installFirmwarePipe()
            .createObservableResult(new InstallFirmwareCommand(new File(filePath)))
            .subscribe(it -> {/*We can't use  OperationDialog, cause we don't use ActionState here*/
               openSuccessScreen();
            }, throwable -> {
               getView().showError();
            });

   }

   private void openSuccessScreen() {
      History.Builder historyBuilder = History.emptyBuilder();
      historyBuilder.push(new CardListPath());
      historyBuilder.push(new WalletSuccessInstallFirmwarePath());
      navigator.setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
   }

   void goToDashboard() {
      navigator.single(new CardListPath(), Flow.Direction.BACKWARD);
   }

   public interface Screen extends WalletScreen {

      void showError();

      void onStart();
   }
}
