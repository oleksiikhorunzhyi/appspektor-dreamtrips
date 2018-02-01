package com.worldventures.wallet.ui.settings.general.firmware.installsuccess.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletSuccessInstallFirmwareScreenImpl::class), complete = false)
class WalletSuccessInstallFirmwareScreenModule {

   @Provides
   fun provideWalletSuccessInstallFirmwarePresenter(navigator: Navigator,
                                                    deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                                    analyticsInteractor: WalletAnalyticsInteractor): WalletSuccessInstallFirmwarePresenter {
      return WalletSuccessInstallFirmwarePresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor)
   }
}
