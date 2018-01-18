package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementMode

interface AgreementStrategy {

   fun create(mode: AgreementMode): AgreementDelegate
}

internal class AppAgreementStrategy(private val analyticsInteractor: WalletAnalyticsInteractor,
                                    private val wizardInteractor: WizardInteractor) : AgreementStrategy {

   override fun create(mode: AgreementMode): AgreementDelegate {
      return when (mode) {
         AgreementMode.AFFIDAVIT -> AffidavitStrategy(wizardInteractor)
         AgreementMode.TAC -> TacStrategy(analyticsInteractor, wizardInteractor)
      }
   }
}

