package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.impl;


import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateSuccessfulAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwareScreen;

import javax.inject.Inject;

public class WalletSuccessInstallFirmwarePresenterImpl extends WalletPresenterImpl<WalletSuccessInstallFirmwareScreen> implements WalletSuccessInstallFirmwarePresenter {

   @Inject WalletAnalyticsInteractor analyticsInteractor;

   public WalletSuccessInstallFirmwarePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletSuccessInstallFirmwareScreen view) {
      super.attachView(view);
      sendAnalyticAction();
      final FirmwareUpdateData firmwareUpdateData = getView().getFirmwareUpdateData();
      getView().setSubTitle(firmwareUpdateData.firmwareInfo().firmwareVersion());
   }

   private void sendAnalyticAction() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateSuccessfulAction()));
   }

   @Override
   public void finishUpdateFlow() {
      if (getView().getFirmwareUpdateData().factoryResetRequired()) {
         getNavigator().finish();
      } else {
         getNavigator().goBack();
      }
   }
}
