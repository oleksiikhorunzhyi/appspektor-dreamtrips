package com.worldventures.wallet.ui.wizard.termsandconditionals

import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.impl.AffidavitStrategy
import com.worldventures.wallet.ui.wizard.termsandconditionals.impl.TacStrategy


interface AgreementStrategy {

   fun init()

   fun loadAgreements(view: WizardTermsScreen)

   fun agreementsAccepted(navigator: Navigator)

   companion object {
      fun create(mode: AgreementMode, analyticsInteractor: WalletAnalyticsInteractor,
                 wizardInteractor: WizardInteractor, staticPageProvider: StaticPageProvider): AgreementStrategy {
         when (mode) {
            AgreementMode.AFFIDAVIT -> return AffidavitStrategy(staticPageProvider)
            AgreementMode.TAC -> return TacStrategy(analyticsInteractor, wizardInteractor)
            else -> throw IllegalArgumentException("Cannot creat AgreementStrategy for received type")
         }
      }
   }
}
