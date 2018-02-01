package com.worldventures.wallet.ui.settings.general.firmware.download.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletDownloadFirmwareScreenImpl::class), complete = false)
class WalletDownloadFirmwareScreenModule {

   @Provides
   fun provideWalletDownloadFirmwarePresenter(navigator: Navigator,
                                              deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                              analyticsInteractor: WalletAnalyticsInteractor,
                                              firmwareInteractor: FirmwareInteractor): WalletDownloadFirmwarePresenter =
         WalletDownloadFirmwarePresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor, firmwareInteractor)
}
