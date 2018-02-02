package com.worldventures.wallet.ui.settings.general.firmware.puck_connection.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletPuckConnectionScreenImpl::class), complete = false)
class WalletPuckConnectionScreenModule {

   @Provides
   fun provideWalletPuckConnectionPresenter(navigator: Navigator,
                                            deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                            smartCardInteractor: SmartCardInteractor): WalletPuckConnectionPresenter {
      return WalletPuckConnectionPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor)
   }
}
