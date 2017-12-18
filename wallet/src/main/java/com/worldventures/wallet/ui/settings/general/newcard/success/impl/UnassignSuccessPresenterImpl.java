package com.worldventures.wallet.ui.settings.general.newcard.success.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.new_smartcard.UnAssignCardSuccessAction;
import com.worldventures.wallet.analytics.new_smartcard.UnAssignCardSuccessGetStartedAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.success.UnassignSuccessScreen;

public class UnassignSuccessPresenterImpl extends WalletPresenterImpl<UnassignSuccessScreen> implements UnassignSuccessPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;

   public UnassignSuccessPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(UnassignSuccessScreen view) {
      super.attachView(view);
      sendAnalyticAction(new UnAssignCardSuccessAction());
   }

   @Override
   public void navigateToWizard() {
      sendAnalyticAction(new UnAssignCardSuccessGetStartedAction());
      getNavigator().goWizardWelcome(ProvisioningMode.SETUP_NEW_CARD);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   private void sendAnalyticAction(WalletAnalyticsAction action) {
      analyticsInteractor
            .walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(action));
   }
}
