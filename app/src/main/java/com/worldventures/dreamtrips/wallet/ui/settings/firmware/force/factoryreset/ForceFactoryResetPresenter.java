package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.factoryreset;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareUpdateType;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.SCFirmwareFacade;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.poweron.ForceUpdatePowerOnPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;

import javax.inject.Inject;

import flow.Flow;

public class ForceFactoryResetPresenter extends WalletPresenter<ForceFactoryResetPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SCFirmwareFacade firmwareFacade;

   public ForceFactoryResetPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      firmwareFacade.prepareForUpdatePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<PrepareForUpdateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> cardPrepared(command.getResult()))
                  .onFail(ErrorHandler.create(getContext(), command -> prepareForUpdate()))
                  .wrap());

      prepareForUpdate();
   }

   private void prepareForUpdate() {
      firmwareFacade.prepareForUpdate();
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
