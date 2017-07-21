package com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.NewCardSetupCompleteAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishScreen;

public class PaymentSyncFinishPresenterImpl extends WalletPresenterImpl<PaymentSyncFinishScreen> implements PaymentSyncFinishPresenter{

   private final WizardInteractor wizardInteractor;
   private final AnalyticsInteractor analyticsInteractor;

   public PaymentSyncFinishPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void onDone() {
      activateSmartCard();
      finishProvisioning();
      sendAnalytic(new NewCardSetupCompleteAction());

      getNavigator().goCardList();
   }

   private void activateSmartCard() {
      wizardInteractor.activateSmartCardPipe().send(new ActivateSmartCardCommand());
   }

   private void finishProvisioning() {
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.clear());
   }

   private void sendAnalytic(WalletAnalyticsAction action) {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(action));
   }
}
