package com.worldventures.wallet.ui.settings.general.reset.success.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.reset.success.FactoryResetSuccessPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(FactoryResetSuccessScreenImpl::class), complete = false)
class FactoryResetSuccessScreenModule {

   @Provides
   fun provideFactoryResetSuccessPresenter(navigator: Navigator,
                                           deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                           analyticsInteractor: WalletAnalyticsInteractor): FactoryResetSuccessPresenter =
         FactoryResetSuccessPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor)
}
