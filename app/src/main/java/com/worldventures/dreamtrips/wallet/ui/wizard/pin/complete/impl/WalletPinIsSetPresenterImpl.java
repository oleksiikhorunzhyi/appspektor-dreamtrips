package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.impl;

import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PinWasSetAction;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetScreen;

public class WalletPinIsSetPresenterImpl extends WalletPresenterImpl<WalletPinIsSetScreen> implements WalletPinIsSetPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WizardInteractor wizardInteractor;

   public WalletPinIsSetPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(WalletPinIsSetScreen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new PinWasSetAction()));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void navigateToNextScreen() {
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.clear());
      wizardInteractor.activateSmartCardPipe().send(new ActivateSmartCardCommand());
      getNavigator().goPaymentSyncFinished();
   }
}
