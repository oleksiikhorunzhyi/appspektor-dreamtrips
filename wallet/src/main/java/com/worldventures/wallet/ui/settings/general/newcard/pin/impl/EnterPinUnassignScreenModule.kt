package com.worldventures.wallet.ui.settings.general.newcard.pin.impl

import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(EnterPinUnassignScreenImpl::class), complete = false)
class EnterPinUnassignScreenModule {

   @Provides
   fun provideEnterPinUnassignPresenter(navigator: Navigator,
                                        deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                        analyticsInteractor: WalletAnalyticsInteractor,
                                        factoryResetInteractor: FactoryResetInteractor): EnterPinUnassignPresenter =
         EnterPinUnassignPresenterImpl(navigator, deviceConnectionDelegate, factoryResetInteractor, analyticsInteractor)
}
