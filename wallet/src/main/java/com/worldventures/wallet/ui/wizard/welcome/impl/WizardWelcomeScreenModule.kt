package com.worldventures.wallet.ui.wizard.welcome.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardWelcomeScreenImpl::class), complete = false)
class WizardWelcomeScreenModule {

   @Provides
   fun provideWizardWelcomePresenter(navigator: Navigator,
                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                     socialInfoProvider: WalletSocialInfoProvider,
                                     analyticsInteractor: WalletAnalyticsInteractor,
                                     wizardInteractor: WizardInteractor): WizardWelcomePresenter {
      return WizardWelcomePresenterImpl(navigator, deviceConnectionDelegate, socialInfoProvider,
            analyticsInteractor, wizardInteractor)
   }
}
