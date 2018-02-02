package com.worldventures.wallet.ui.wizard.splash.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.splash.WizardSplashPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardSplashScreenImpl::class), complete = false)
class WizardSplashScreenModule {

   @Provides
   fun provideWizardSplashPresenter(navigator: Navigator,
                                    deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                    analyticsInteractor: WalletAnalyticsInteractor): WizardSplashPresenter =
         WizardSplashPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor)
}
