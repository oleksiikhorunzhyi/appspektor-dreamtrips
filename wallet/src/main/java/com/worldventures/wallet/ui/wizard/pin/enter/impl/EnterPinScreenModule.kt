package com.worldventures.wallet.ui.wizard.pin.enter.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pin.enter.EnterPinPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(EnterPinScreenImpl::class), complete = false)
class EnterPinScreenModule {

   @Provides
   fun provideEnterPinPresenter(navigator: Navigator,
                                deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                wizardInteractor: WizardInteractor,
                                analyticsInteractor: WalletAnalyticsInteractor): EnterPinPresenter =
         EnterPinPresenterImpl(navigator, deviceConnectionDelegate, wizardInteractor, analyticsInteractor)
}
