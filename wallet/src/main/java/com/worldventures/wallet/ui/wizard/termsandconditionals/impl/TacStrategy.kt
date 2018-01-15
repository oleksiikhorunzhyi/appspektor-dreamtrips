package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.TermsAcceptedAction
import com.worldventures.wallet.analytics.wizard.TermsAction
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementMode

class TacStrategy(private val analyticsInteractor: WalletAnalyticsInteractor,
                  wizardInteractor: WizardInteractor) : AgreementDelegate(wizardInteractor) {

   override fun trackScreen() {
      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(TermsAction()))
   }

   override fun agreementsAccepted(navigator: Navigator) {
      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(TermsAcceptedAction()))
      navigator.goWizardSplash()
   }

   override fun provideAgreementDocumentType(): AgreementMode {
      return AgreementMode.TAC
   }
}
