package com.worldventures.wallet.ui.settings.general.reset.impl

import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(FactoryResetScreenImpl::class), complete = false)
class FactoryResetScreenModule {

   @Provides
   fun provideFactoryResetPresenter(navigator: Navigator,
                                    deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                    analyticsInteractor: WalletAnalyticsInteractor,
                                    factoryResetInteractor: FactoryResetInteractor): FactoryResetPresenter =
         FactoryResetPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor, factoryResetInteractor)
}
