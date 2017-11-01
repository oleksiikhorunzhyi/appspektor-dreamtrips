package com.worldventures.wallet.ui.wizard.termsandconditionals.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.TermsAcceptedAction;
import com.worldventures.wallet.analytics.wizard.TermsAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter;
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WizardTermsPresenterImpl extends WalletPresenterImpl<WizardTermsScreen> implements WizardTermsPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WizardInteractor wizardInteractor;

   public WizardTermsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
      this.wizardInteractor = wizardInteractor;
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
      wizardInteractor.getTermsAndConditionsPipe()
            .createObservable(new FetchTermsAndConditionsCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().termsOperationView())
                  .onSuccess(command -> getView().showTerms(command.getResult().getUrl()))
                  .create());
   }

   @Override
   public void onBack() {
      getNavigator().goBack();
   }
}
