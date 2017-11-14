package com.worldventures.wallet.ui.common

import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.screen.WalletScreen

class MockDeviceConnectionDelegate : WalletDeviceConnectionDelegate {
   override fun setup(view: WalletScreen) {
      //no-op
   }
}
