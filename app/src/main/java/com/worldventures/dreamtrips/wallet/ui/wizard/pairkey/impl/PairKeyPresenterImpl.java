package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CardConnectedAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CheckFrontAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class PairKeyPresenterImpl extends WalletPresenterImpl<PairKeyScreen> implements PairKeyPresenter {

   private final WizardInteractor wizardInteractor;
   private final AnalyticsInteractor analyticsInteractor;

   private PairDelegate pairDelegate;

   public PairKeyPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(PairKeyScreen view) {
      super.attachView(view);
      this.pairDelegate = PairDelegate.create(getView().getProvisionMode(), getNavigator(), getSmartCardInteractor());
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

   @Override
   public void tryToPairAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand(getView().getBarcode()));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
