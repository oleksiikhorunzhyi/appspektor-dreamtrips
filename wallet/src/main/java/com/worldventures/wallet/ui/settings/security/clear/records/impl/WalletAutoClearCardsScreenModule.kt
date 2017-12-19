package com.worldventures.wallet.ui.settings.security.clear.records.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.security.clear.common.items.AutoClearSmartCardItemProvider
import com.worldventures.wallet.ui.settings.security.clear.records.WalletAutoClearCardsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletAutoClearCardsScreenImpl::class), complete = false)
class WalletAutoClearCardsScreenModule {

   @Provides
   fun provideWalletAutoClearCardsPresenter(navigator: Navigator,
                                            deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                            smartCardInteractor: SmartCardInteractor,
                                            analyticsInteractor: WalletAnalyticsInteractor,
                                            itemProvider: AutoClearSmartCardItemProvider): WalletAutoClearCardsPresenter =
         WalletAutoClearCardsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
               analyticsInteractor, itemProvider)
}
