package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.impl;


import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.ViewSdkVersionAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwareScreen;

public class WalletUpToDateFirmwarePresenterImpl extends WalletPresenterImpl<WalletUpToDateFirmwareScreen> implements WalletUpToDateFirmwarePresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;

   public WalletUpToDateFirmwarePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletUpToDateFirmwareScreen view) {
      super.attachView(view);
      observeSmartCard();
      sendAnalyticViewAction();
   }

   private void observeSmartCard() {
      getSmartCardInteractor().smartCardFirmwarePipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
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
