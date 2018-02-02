package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen

class WizardTermsPresenterImpl(navigator: Navigator,
                               deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                               private val agreementStrategy: AgreementStrategy)
   : WalletPresenterImpl<WizardTermsScreen>(navigator, deviceConnectionDelegate), WizardTermsPresenter {

   private lateinit var agreementDelegate: AgreementDelegate

   override fun attachView(view: WizardTermsScreen) {
      super.attachView(view)
      agreementDelegate = agreementStrategy.create(view.agreementMode)
      agreementDelegate.trackScreen()
   }

   override fun acceptTermsPressed() {
      agreementDelegate.agreementsAccepted(navigator)
   }

   override fun loadTerms() {
      agreementDelegate.loadAgreements(view)
   }

   override fun onBack() {
      navigator.goBack()
   }
}
