package com.worldventures.wallet.ui.wizard.termsandconditionals

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WizardTermsPresenter : WalletPresenter<WizardTermsScreen> {

   fun onBack()

   fun loadTerms()

   fun acceptTermsPressed()

}
