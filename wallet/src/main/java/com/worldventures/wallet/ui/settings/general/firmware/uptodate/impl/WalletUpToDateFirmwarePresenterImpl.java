package com.worldventures.wallet.ui.settings.general.firmware.uptodate.impl;


import com.worldventures.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.wallet.analytics.firmware.action.ViewSdkVersionAction;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwareScreen;

import rx.android.schedulers.AndroidSchedulers;

public class WalletUpToDateFirmwarePresenterImpl extends WalletPresenterImpl<WalletUpToDateFirmwareScreen> implements WalletUpToDateFirmwarePresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public WalletUpToDateFirmwarePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletUpToDateFirmwareScreen view) {
      super.attachView(view);
      observeSmartCard();
      sendAnalyticViewAction();
   }

   private void observeSmartCard() {
      smartCardInteractor.smartCardFirmwarePipe()
            .observeSuccessWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> bindSmartCardFirmware(command.getResult()));
   }

   private void sendAnalyticViewAction() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new ViewSdkVersionAction()));
   }

   private void bindSmartCardFirmware(SmartCardFirmware smartCardFirmware) {
      getView().version(smartCardFirmware);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
