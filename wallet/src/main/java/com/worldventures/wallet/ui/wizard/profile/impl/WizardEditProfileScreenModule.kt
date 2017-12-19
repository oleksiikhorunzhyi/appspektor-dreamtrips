package com.worldventures.wallet.ui.wizard.profile.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfilePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardEditProfileScreenImpl::class), complete = false)
class WizardEditProfileScreenModule {

   @Provides
   fun provideWizardProfilePresenter(navigator: Navigator,
                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                     smartCardInteractor: SmartCardInteractor,
                                     analyticsInteractor: WalletAnalyticsInteractor,
                                     wizardInteractor: WizardInteractor,
                                     socialInfoProvider: WalletSocialInfoProvider,
                                     smartCardUserDataInteractor: SmartCardUserDataInteractor): WizardEditProfilePresenter {
      return WizardEditProfilePresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, wizardInteractor,
            analyticsInteractor, socialInfoProvider, smartCardUserDataInteractor)
   }
}
