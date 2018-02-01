package com.worldventures.wallet.ui.settings.general.firmware.start.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(StartFirmwareInstallScreenImpl::class), complete = false)
class StartFirmwareInstallScreenModule {

   @Provides
   fun provideStartFirmwareInstallPresenter(navigator: Navigator,
                                            deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                            firmwareInteractor: FirmwareInteractor): StartFirmwareInstallPresenter =
         StartFirmwareInstallPresenterImpl(navigator, deviceConnectionDelegate, firmwareInteractor)

}
