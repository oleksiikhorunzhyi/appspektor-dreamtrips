package com.worldventures.wallet.ui.settings.general.firmware.installsuccess.impl;


import com.worldventures.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.wallet.analytics.firmware.action.UpdateSuccessfulAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwareScreen;

import javax.inject.Inject;

public class WalletSuccessInstallFirmwarePresenterImpl extends WalletPresenterImpl<WalletSuccessInstallFirmwareScreen> implements WalletSuccessInstallFirmwarePresenter {

   @Inject WalletAnalyticsInteractor analyticsInteractor;

   public WalletSuccessInstallFirmwarePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletSuccessInstallFirmwareScreen view) {
      super.attachView(view);
      sendAnalyticAction();
      final FirmwareUpdateData firmwareUpdateData = getView().getFirmwareUpdateData();
      getView().setSubTitle(firmwareUpdateData.getFirmwareInfo().firmwareVersion());
   }

   private void sendAnalyticAction() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateSuccessfulAction()));
   }

   @Override
   public void finishUpdateFlow() {
      if (getView().getFirmwareUpdateData().isFactoryResetRequired()) {
         getNavigator().finish();
      } else {
         getNavigator().goBack();
      }
   }
}
