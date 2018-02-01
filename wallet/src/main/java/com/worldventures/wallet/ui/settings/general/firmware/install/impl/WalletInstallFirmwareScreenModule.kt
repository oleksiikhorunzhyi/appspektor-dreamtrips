package com.worldventures.wallet.ui.settings.general.firmware.install.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletInstallFirmwareScreenImpl::class), complete = false)
class WalletInstallFirmwareScreenModule {

   @Provides
   internal fun provideWalletInstallFirmwarePresenter(navigator: Navigator,
                                                      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                                      firmwareInteractor: FirmwareInteractor,
                                                      analyticsInteractor: WalletAnalyticsInteractor): WalletInstallFirmwarePresenter =
         WalletInstallFirmwarePresenterImpl(navigator, deviceConnectionDelegate, firmwareInteractor, analyticsInteractor)

}
