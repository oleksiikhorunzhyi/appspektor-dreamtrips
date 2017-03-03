package com.worldventures.dreamtrips.wallet.ui.wizard.paymentcomplete;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.NewCardSetupCompleteAction;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;

import javax.inject.Inject;

import flow.Flow;

public class PaymentSyncFinishPresenter extends WalletPresenter<PaymentSyncFinishPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;

   public PaymentSyncFinishPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void onDone() {
      analyticsInteractor
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new NewCardSetupCompleteAction()));

      navigator.single(new CardListPath(), Flow.Direction.REPLACE);
   }

   public interface Screen extends WalletScreen {
   }
}
