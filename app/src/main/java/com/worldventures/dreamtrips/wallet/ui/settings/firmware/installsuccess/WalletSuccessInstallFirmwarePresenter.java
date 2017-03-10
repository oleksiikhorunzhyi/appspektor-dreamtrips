package com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateSuccessfulAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletSuccessInstallFirmwarePresenter extends WalletPresenter<WalletSuccessInstallFirmwarePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final FirmwareUpdateData firmwareUpdateData;

   public WalletSuccessInstallFirmwarePresenter(Context context, Injector injector, FirmwareUpdateData firmwareUpdateData) {
      super(context, injector);
      this.firmwareUpdateData = firmwareUpdateData;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      sendAnalyticAction();
      getView().setSubTitle(firmwareUpdateData.firmwareInfo().firmwareVersion());
   }

   private void sendAnalyticAction() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateSuccessfulAction()));
   }

   void finishUpdateFlow() {
      if (firmwareUpdateData.factoryResetRequired()) {
         navigator.finish();
      } else {
         navigator.goBack();
      }
   }

   public interface Screen extends WalletScreen {

      void setSubTitle(String version);
   }
}
