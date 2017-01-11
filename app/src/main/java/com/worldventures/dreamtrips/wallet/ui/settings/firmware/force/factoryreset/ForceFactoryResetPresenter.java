package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.factoryreset;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.FactoryResetManager;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
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

   @Inject FactoryResetManager factoryResetManager;
   @Inject Navigator navigator;

   private final SmartCard smartCard;
   private final FirmwareUpdateData firmwareUpdateData;

   public ForceFactoryResetPresenter(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData, Context context, Injector injector) {
      super(context, injector);
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      if (firmwareUpdateData.factoryResetRequired()) {
         factoryResetSmartCard();
      } else {
         goToFWUpdate();
      }
   }

   private void factoryResetSmartCard() {
      factoryResetManager.factoryResetCommandActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<FactoryResetCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> goToConnectionInstructions())
                  .onFail(ErrorHandler.create(getContext(), command -> navigator.finish()))
                  .wrap()
            );

      factoryResetManager.factoryResetCommandActionPipe().send(new FactoryResetCommand(false));
   }

   private void goToConnectionInstructions() {
      navigator.single(new ForceUpdatePowerOnPath(smartCard, firmwareUpdateData), Flow.Direction.REPLACE);
   }

   private void goToFWUpdate() {
      navigator.single(new WalletNewFirmwareAvailablePath(smartCard, firmwareUpdateData), Flow.Direction.REPLACE);
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }
}
