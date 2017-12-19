package com.worldventures.wallet.ui.settings.security.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsPresenter
import com.worldventures.wallet.util.WalletFeatureHelper
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletSecuritySettingsScreenImpl::class), complete = false)
class WalletSecuritySettingsScreenModule {

   @Provides
   fun provideWalletSecuritySettingsPresenter(navigator: Navigator,
                                              deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                              smartCardInteractor: SmartCardInteractor,
                                              analyticsInteractor: WalletAnalyticsInteractor,
                                              walletFeatureHelper: WalletFeatureHelper): WalletSecuritySettingsPresenter =
         WalletSecuritySettingsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
               analyticsInteractor, walletFeatureHelper)
}
