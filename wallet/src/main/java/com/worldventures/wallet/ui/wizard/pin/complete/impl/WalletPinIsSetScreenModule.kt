package com.worldventures.wallet.ui.wizard.pin.complete.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletPinIsSetScreenImpl::class), complete = false)

class WalletPinIsSetScreenModule {

   @Provides
   fun provideWalletPinIsSetPresenter(navigator: Navigator,
                                      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                      analyticsInteractor: WalletAnalyticsInteractor,
                                      wizardInteractor: WizardInteractor): WalletPinIsSetPresenter =
         WalletPinIsSetPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor, wizardInteractor)
}
