package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementMode

class AffidavitStrategy(wizardInteractor: WizardInteractor) : AgreementDelegate(wizardInteractor) {

   override fun agreementsAccepted(navigator: Navigator) {
      navigator.goWizardTerms()
   }

   override fun provideAgreementDocumentType(): AgreementMode {
      return AgreementMode.AFFIDAVIT
   }
}
