package com.worldventures.wallet.ui.settings.general.firmware.newavailable.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletNewFirmwareAvailableScreenImpl::class), complete = false)
class WalletNewFirmwareAvailableScreenModule {

   @Provides
   fun provideWalletNewFirmwareAvailablePresenter(navigator: Navigator,
                                                  deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                                  firmwareInteractor: FirmwareInteractor,
                                                  analyticsInteractor: WalletAnalyticsInteractor): WalletNewFirmwareAvailablePresenter {
      return WalletNewFirmwareAvailablePresenterImpl(navigator, deviceConnectionDelegate,
            firmwareInteractor, analyticsInteractor)
   }
}
