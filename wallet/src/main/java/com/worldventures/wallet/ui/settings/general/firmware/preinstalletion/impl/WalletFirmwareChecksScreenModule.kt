package com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletFirmwareChecksScreenImpl::class), complete = false)
class WalletFirmwareChecksScreenModule {

   @Provides
   fun provideWalletFirmwareChecksPresenter(navigator: Navigator,
                                            deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                            smartCardInteractor: SmartCardInteractor,
                                            walletBluetoothService: WalletBluetoothService,
                                            firmwareInteractor: FirmwareInteractor,
                                            analyticsInteractor: WalletAnalyticsInteractor): WalletFirmwareChecksPresenter {
      return WalletFirmwareChecksPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            walletBluetoothService, firmwareInteractor, analyticsInteractor)
   }
}
