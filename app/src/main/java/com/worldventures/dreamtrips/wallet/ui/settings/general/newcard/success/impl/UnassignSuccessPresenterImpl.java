package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.UnAssignCardSuccessAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.UnAssignCardSuccessGetStartedAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessScreen;

public class UnassignSuccessPresenterImpl extends WalletPresenterImpl<UnassignSuccessScreen> implements UnassignSuccessPresenter {

   private final AnalyticsInteractor analyticsInteractor;

   public UnassignSuccessPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
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
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(action));
   }
}
