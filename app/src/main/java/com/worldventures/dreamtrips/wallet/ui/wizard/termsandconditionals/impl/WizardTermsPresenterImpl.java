package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.impl;


import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.TermsAcceptedAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.TermsAction;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsScreen;

import io.techery.janet.Janet;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class WizardTermsPresenterImpl extends WalletPresenterImpl<WizardTermsScreen> implements WizardTermsPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final Janet janet;

   public WizardTermsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, Janet janet) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
      this.janet = janet;
   }

   @Override
   public void attachView(WizardTermsScreen view) {
      super.attachView(view);
      loadTerms();
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new TermsAction()));
   }

   @Override
   public void acceptTermsPressed() {
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new TermsAcceptedAction()));
      getNavigator().goWizardSplash();
   }

   @Override
   public void loadTerms() {
      janet.createPipe(FetchTermsAndConditionsCommand.class)
            .createObservable(new FetchTermsAndConditionsCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().termsOperationView())
                  .onSuccess(command -> getView().showTerms(command.getResult().url()))
                  .create());
   }

   @Override
   public void onBack() {
      getNavigator().goBack();
   }
}
