package com.worldventures.wallet.ui.settings.general.firmware.reset.pair.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(ForcePairKeyScreenImpl::class), complete = false)
class ForcePairKeyScreenModule {

   @Provides
   fun provideForcePairKeyPresenter(navigator: Navigator,
                                    deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                    firmwareInteractor: FirmwareInteractor): ForcePairKeyPresenter {
      return ForcePairKeyPresenterImpl(navigator, deviceConnectionDelegate, firmwareInteractor)
   }
}
