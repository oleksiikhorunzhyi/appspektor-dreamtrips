package com.worldventures.wallet.ui.settings.security.clear.default_card.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.security.clear.common.items.DisableDefaultCardItemProvider
import com.worldventures.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletDisableDefaultCardScreenImpl::class), complete = false)
class WalletDisableDefaultCardScreenModule {

   @Provides
   fun provideWalletDisableDefaultCardPresenter(navigator: Navigator,
                                                deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                                smartCardInteractor: SmartCardInteractor,
                                                analyticsInteractor: WalletAnalyticsInteractor,
                                                itemProvider: DisableDefaultCardItemProvider): WalletDisableDefaultCardPresenter =
         WalletDisableDefaultCardPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
               analyticsInteractor, itemProvider)
}
