package com.worldventures.wallet.ui.settings.security.offline_mode.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletOfflineModeSettingsScreenImpl::class), complete = false)
class WalletOfflineModeSettingsScreenModule {

   @Provides
   fun provideWalletOfflineModeSettingsPresenter(navigator: Navigator,
                                                 deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                                 smartCardInteractor: SmartCardInteractor,
                                                 networkDelegate: WalletNetworkDelegate,
                                                 analyticsInteractor: WalletAnalyticsInteractor): WalletOfflineModeSettingsPresenter =
         WalletOfflineModeSettingsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, networkDelegate, analyticsInteractor)

}
