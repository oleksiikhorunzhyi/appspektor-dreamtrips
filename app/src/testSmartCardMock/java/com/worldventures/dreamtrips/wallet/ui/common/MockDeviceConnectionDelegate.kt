package com.worldventures.dreamtrips.wallet.ui.common

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen

class MockDeviceConnectionDelegate : WalletDeviceConnectionDelegate {
   override fun setup(view: WalletScreen) {
      //no-op
   }
}
