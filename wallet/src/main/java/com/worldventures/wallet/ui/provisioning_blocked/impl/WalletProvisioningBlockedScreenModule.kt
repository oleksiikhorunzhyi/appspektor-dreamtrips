package com.worldventures.wallet.ui.provisioning_blocked.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletProvisioningBlockedScreenImpl::class), complete = false)
class WalletProvisioningBlockedScreenModule {

   @Provides
   fun provideWalletBlockedPresenter(navigator: Navigator,
                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                     smartCardInteractor: SmartCardInteractor,
                                     analyticsInteractor: WalletAnalyticsInteractor): WalletProvisioningBlockedPresenter {
      return WalletProvisioningBlockedPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor)
   }
}
