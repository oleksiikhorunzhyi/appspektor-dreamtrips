package com.worldventures.dreamtrips.wallet.ui.wizard.records.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.NewCardSetupCompleteAction;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;

import javax.inject.Inject;

import flow.Flow;

public class PaymentSyncFinishPresenter extends WalletPresenter<PaymentSyncFinishPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public PaymentSyncFinishPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void onDone() {
      activateSmartCard();
      finishProvisioning();
      sendAnalytic(new NewCardSetupCompleteAction());

      navigator.single(new CardListPath(), Flow.Direction.REPLACE);
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

   public interface Screen extends WalletScreen {
   }
}
