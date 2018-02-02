package com.worldventures.wallet.ui.settings.help.impl

import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.WalletHelpSettingsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletHelpSettingsScreenImpl::class), complete = false)
class WalletHelpSettingsScreenModule {

   @Provides
   fun provideWalletHelpSettingsPresenter(navigator: Navigator,
                                          deviceConnectionDelegate: WalletDeviceConnectionDelegate): WalletHelpSettingsPresenter =
         WalletHelpSettingsPresenterImpl(navigator, deviceConnectionDelegate)
}
