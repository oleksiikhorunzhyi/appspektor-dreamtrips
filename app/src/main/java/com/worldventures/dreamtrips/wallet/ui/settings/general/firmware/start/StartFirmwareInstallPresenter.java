package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareUpdateType;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPath;

import javax.inject.Inject;

import flow.Flow;

public class StartFirmwareInstallPresenter extends WalletPresenter<StartFirmwareInstallPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject FirmwareInteractor firmwareInteractor;

   public StartFirmwareInstallPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      firmwareInteractor.prepareForUpdatePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<PrepareForUpdateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> cardPrepared(command.getResult()))
                  .onFail(ErrorHandler.create(getContext(), command -> prepareForUpdate()))
                  .wrap());

      prepareForUpdate();
   }

   private void prepareForUpdate() {
      firmwareInteractor.prepareForUpdatePipe().send(new PrepareForUpdateCommand());
   }

   private void cardPrepared(FirmwareUpdateType type) {
      if (type == FirmwareUpdateType.CRITICAL) {
         goToConnectionInstructions();
      } else {
         goToFWUpdate();
      }
   }

   private void goToConnectionInstructions() {
      navigator.single(new ForceUpdatePowerOnPath(), Flow.Direction.REPLACE);
   }

   private void goToFWUpdate() {
      navigator.single(new WalletNewFirmwareAvailablePath(), Flow.Direction.REPLACE);
   }

   public void goBack() {
      navigator.goBack();
   }

   public void finish() {
      navigator.finish();
   }

   public interface Screen extends WalletScreen {
   }
}
