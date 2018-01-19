package com.worldventures.wallet.ui.wizard.pin.success.impl

import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pin.success.PinSetSuccessPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(PinSetSuccessScreenImpl::class), complete = false)
class PinSetSuccessScreenModule {

   @Provides
   fun providePinSetSuccessPresenter(navigator: Navigator,
                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate): PinSetSuccessPresenter =
         PinSetSuccessPresenterImpl(navigator, deviceConnectionDelegate)
}
