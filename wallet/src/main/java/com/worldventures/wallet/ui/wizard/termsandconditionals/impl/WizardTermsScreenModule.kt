package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardTermsScreenImpl::class), complete = false)
class WizardTermsScreenModule {

   @Provides
   fun provideWizardTermsPresenter(navigator: Navigator,
                                   deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                   analyticsInteractor: WalletAnalyticsInteractor,
                                   wizardInteractor: WizardInteractor): WizardTermsPresenter {
      return WizardTermsPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor, wizardInteractor)
   }
}
