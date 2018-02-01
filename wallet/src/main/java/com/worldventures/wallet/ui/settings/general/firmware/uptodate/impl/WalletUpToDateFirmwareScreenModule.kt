package com.worldventures.wallet.ui.settings.general.firmware.uptodate.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletUpToDateFirmwareScreenImpl::class), complete = false)
class WalletUpToDateFirmwareScreenModule {

   @Provides
   fun providesWalletUpToDateFirmwarePresenter(navigator: Navigator,
                                               deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                               smartCardInteractor: SmartCardInteractor,
                                               analyticsInteractor: WalletAnalyticsInteractor): WalletUpToDateFirmwarePresenter {
      return WalletUpToDateFirmwarePresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor)
   }
}
