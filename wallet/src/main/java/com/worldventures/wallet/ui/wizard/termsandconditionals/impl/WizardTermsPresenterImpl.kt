package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementStrategy
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen

class WizardTermsPresenterImpl(navigator: Navigator,
                               deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                               private val analyticsInteractor: WalletAnalyticsInteractor,
                               private val wizardInteractor: WizardInteractor,
                               private val staticPageProvider: StaticPageProvider)
   : WalletPresenterImpl<WizardTermsScreen>(navigator, deviceConnectionDelegate), WizardTermsPresenter {

   private lateinit var agreementStrategy: AgreementStrategy

   override fun attachView(view: WizardTermsScreen) {
      super.attachView(view)
      agreementStrategy = AgreementStrategy.create(view.agreementMode, analyticsInteractor,
            wizardInteractor, staticPageProvider)
      agreementStrategy.init()
      loadTerms()
   }

   override fun acceptTermsPressed() {
      agreementStrategy.agreementsAccepted(navigator)
   }

   override fun loadTerms() {
      agreementStrategy.loadAgreements(view)
   }

   override fun onBack() {
      navigator.goBack()
   }
}
