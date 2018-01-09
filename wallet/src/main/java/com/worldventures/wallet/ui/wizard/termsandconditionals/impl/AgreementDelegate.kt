package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen

interface AgreementDelegate {

   fun trackScreen() {
//      nothing
   }

   fun loadAgreements(view: WizardTermsScreen)

   fun agreementsAccepted(navigator: Navigator)
}
