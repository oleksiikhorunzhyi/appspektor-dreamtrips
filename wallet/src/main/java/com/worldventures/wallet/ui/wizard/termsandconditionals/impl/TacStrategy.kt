package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.TermsAcceptedAction
import com.worldventures.wallet.analytics.wizard.TermsAction
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.http.FetchTermsAndConditionsCommand
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementStrategy
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers


class TacStrategy(private val analyticsInteractor: WalletAnalyticsInteractor,
                  private val wizardInteractor: WizardInteractor): AgreementStrategy {

   override fun init() {
      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(TermsAction()))
   }

   override fun loadAgreements(view: WizardTermsScreen) {
      wizardInteractor.termsAndConditionsPipe
            .createObservable(FetchTermsAndConditionsCommand())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.termsOperationView())
                  .onSuccess { command -> view.showTerms(command.result.url) }
                  .create())
   }

   override fun agreementsAccepted(navigator: Navigator) {
      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(TermsAcceptedAction()))
      navigator.goWizardSplash()
   }
}
