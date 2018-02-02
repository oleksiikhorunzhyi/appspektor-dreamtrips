package com.worldventures.wallet.ui.settings.general.newcard.success.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(UnassignSuccessScreenImpl::class), complete = false)
class UnassignSuccessScreenModule {

   @Provides
   fun provideUnassignSuccessPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       analyticsInteractor: WalletAnalyticsInteractor): UnassignSuccessPresenter =
         UnassignSuccessPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor)
}
