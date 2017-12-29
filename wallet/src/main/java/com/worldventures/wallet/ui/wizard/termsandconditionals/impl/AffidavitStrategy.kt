package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementStrategy
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen


class AffidavitStrategy(private val staticPageProvider: StaticPageProvider): AgreementStrategy {

   override fun init() {
      //do nothing
   }

   override fun loadAgreements(view: WizardTermsScreen) {
      view.showTerms(staticPageProvider.smartcardAffidavitUrl)
   }

   override fun agreementsAccepted(navigator: Navigator) {
      navigator.goWizardTerms()
   }

}
