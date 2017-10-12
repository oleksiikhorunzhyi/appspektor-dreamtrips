package com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.impl;


import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.NewCardSetupCompleteAction;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishScreen;

public class PaymentSyncFinishPresenterImpl extends WalletPresenterImpl<PaymentSyncFinishScreen> implements PaymentSyncFinishPresenter {

   private final WizardInteractor wizardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public PaymentSyncFinishPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
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
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(action));
   }
}
