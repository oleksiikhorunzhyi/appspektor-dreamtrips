package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CardConnectedAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CheckFrontAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.ProvisioningMode;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class PairKeyPresenter extends WalletPresenter<PairKeyPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final PairDelegate pairDelegate;
   private final String barcode;

   public PairKeyPresenter(Context context, Injector injector, ProvisioningMode provisioningMode, String barcode) {
      super(context, injector);
      this.pairDelegate = PairDelegate.create(provisioningMode, navigator, smartCardInteractor);
      this.barcode = barcode;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      pairDelegate.prepareView(getView());

      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new CheckFrontAction()));

      observeCreateAndConnectSmartCard();
   }

   private void observeCreateAndConnectSmartCard() {
      //noinspection ConstantConditions
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationCreateAndConnect())
                  .onSuccess(command -> smartCardConnected())
                  .create());
   }

   private void smartCardConnected() {
      pairDelegate.navigateOnNextScreen(getView());
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new CardConnectedAction()));
   }

   void tryToPairAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand(barcode));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen, PairView {

      OperationView<CreateAndConnectToCardCommand> provideOperationCreateAndConnect();
   }
}
