package com.worldventures.wallet.ui.wizard.pairkey.impl;


import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.CardConnectedAction;
import com.worldventures.wallet.analytics.wizard.CheckFrontAction;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.pairkey.PairDelegate;
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

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
                  .onFail((command, throwable) -> {
                     Timber.e(throwable, "");
                     getView().nextButtonEnable(true);
                  })
                  .create());
   }

   private void smartCardConnected() {
      pairDelegate.navigateOnNextScreen(getView());
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new CardConnectedAction()));
   }

   @Override
   public void tryToPairAndConnectSmartCard() {
      getView().nextButtonEnable(false);
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand(getView().getBarcode()));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
