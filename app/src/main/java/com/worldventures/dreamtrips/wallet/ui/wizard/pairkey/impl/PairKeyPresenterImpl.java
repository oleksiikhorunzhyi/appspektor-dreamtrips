package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.impl;


import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CardConnectedAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CheckFrontAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class PairKeyPresenterImpl extends WalletPresenterImpl<PairKeyScreen> implements PairKeyPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WizardInteractor wizardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   private PairDelegate pairDelegate;

   public PairKeyPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WizardInteractor wizardInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(PairKeyScreen view) {
      super.attachView(view);
      this.pairDelegate = PairDelegate.create(getView().getProvisionMode(), getNavigator(), smartCardInteractor);
      pairDelegate.prepareView(getView());
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new CheckFrontAction()));
      observeCreateAndConnectSmartCard();
   }

   private void observeCreateAndConnectSmartCard() {
      //noinspection ConstantConditions
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationCreateAndConnect())
                  .onSuccess(command -> smartCardConnected())
                  .create());
   }

   private void smartCardConnected() {
      pairDelegate.navigateOnNextScreen(getView());
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new CardConnectedAction()));
   }

   @Override
   public void tryToPairAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand(getView().getBarcode()));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
