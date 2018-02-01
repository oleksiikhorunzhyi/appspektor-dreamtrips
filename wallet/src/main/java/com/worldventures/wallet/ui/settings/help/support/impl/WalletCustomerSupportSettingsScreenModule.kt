package com.worldventures.wallet.ui.settings.help.support.impl

import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.support.WalletCustomerSupportSettingsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletCustomerSupportSettingsScreenImpl::class), complete = false)
class WalletCustomerSupportSettingsScreenModule {

   @Provides
   fun provideWalletCustomerSupportSettingsPresenter(navigator: Navigator,
                                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                                     walletSettingsInteractor: WalletSettingsInteractor,
                                                     httpErrorHandlingUtil: HttpErrorHandlingUtil): WalletCustomerSupportSettingsPresenter =
         WalletCustomerSupportSettingsPresenterImpl(navigator, deviceConnectionDelegate, walletSettingsInteractor, httpErrorHandlingUtil)

}
