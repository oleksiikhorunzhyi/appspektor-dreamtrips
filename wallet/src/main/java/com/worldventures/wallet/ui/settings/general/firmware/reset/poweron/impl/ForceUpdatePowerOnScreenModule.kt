package com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.impl

import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(ForceUpdatePowerOnScreenImpl::class), complete = false)
class ForceUpdatePowerOnScreenModule {

   @Provides
   fun provideForceUpdatePowerOnPresenter(navigator: Navigator,
                                          deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                          networkDelegate: WalletNetworkDelegate,
                                          walletBluetoothService: WalletBluetoothService): ForceUpdatePowerOnPresenter =
         ForceUpdatePowerOnPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate, walletBluetoothService)

}
