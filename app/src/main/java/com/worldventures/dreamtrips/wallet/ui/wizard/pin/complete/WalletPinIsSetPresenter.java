package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.PinWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WizardAssignUserPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.paymentcards.SyncPaymentCardPath;

import javax.inject.Inject;

import timber.log.Timber;

public class WalletPinIsSetPresenter extends WalletPresenter<WalletPinIsSetPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SmartCardInteractor smartCardInteractor;

   public WalletPinIsSetPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe()
            // TODO: 2/20/17
            .send(new WalletAnalyticsCommand(new PinWasSetAction(null)));
   }

   public void goBack() {
      navigator.goBack();
   }

   void activateSmartCard() {
      smartCardInteractor.cardsListPipe()
            .createObservableResult(new CardListCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> navigateToNextScreen(!command.getCacheData()
                  .isEmpty()), throwable -> Timber.e(throwable, ""));
   }

   private void navigateToNextScreen(boolean needToOpenSyncPaymentsScreen) {
      if (needToOpenSyncPaymentsScreen) {
         navigator.go(new SyncPaymentCardPath());
      } else {
         navigator.go(new WizardAssignUserPath());
      }
   }

   public interface Screen extends WalletScreen {

   }
}
