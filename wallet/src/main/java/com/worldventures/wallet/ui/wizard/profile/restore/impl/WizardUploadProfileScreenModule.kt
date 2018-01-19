package com.worldventures.wallet.ui.wizard.profile.restore.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardUploadProfileScreenImpl::class), complete = false)
class WizardUploadProfileScreenModule {

   @Provides
   fun provideWizardUploadProfilePresenter(navigator: Navigator,
                                           deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                           smartCardInteractor: SmartCardInteractor,
                                           analyticsInteractor: WalletAnalyticsInteractor,
                                           wizardInteractor: WizardInteractor): WizardUploadProfilePresenter {
      return WizardUploadProfilePresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            wizardInteractor, analyticsInteractor)
   }
}
