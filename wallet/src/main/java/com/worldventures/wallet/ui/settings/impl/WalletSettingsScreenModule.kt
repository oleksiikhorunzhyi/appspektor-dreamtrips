package com.worldventures.wallet.ui.settings.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.WalletSettingsPresenter
import com.worldventures.wallet.util.WalletFeatureHelper
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletSettingsScreenImpl::class), complete = false)
class WalletSettingsScreenModule {

   @Provides
   fun provideWalletSettingsPresenter(navigator: Navigator,
                                      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                      smartCardInteractor: SmartCardInteractor,
                                      firmwareInteractor: FirmwareInteractor,
                                      analyticsInteractor: WalletAnalyticsInteractor,
                                      walletFeatureHelper: WalletFeatureHelper): WalletSettingsPresenter =
         WalletSettingsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, firmwareInteractor,
               analyticsInteractor, walletFeatureHelper)

}
