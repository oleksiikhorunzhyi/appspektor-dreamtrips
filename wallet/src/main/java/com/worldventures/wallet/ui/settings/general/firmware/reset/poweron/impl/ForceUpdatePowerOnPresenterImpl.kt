package com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.impl

import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnScreen

class ForceUpdatePowerOnPresenterImpl(navigator: Navigator,
                                      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                      private val networkDelegate: WalletNetworkDelegate,
                                      private val bluetoothService: WalletBluetoothService)
   : WalletPresenterImpl<ForceUpdatePowerOnScreen>(navigator, deviceConnectionDelegate), ForceUpdatePowerOnPresenter {

   override fun attachView(view: ForceUpdatePowerOnScreen) {
      super.attachView(view)
      networkDelegate.setup(view)
   }

   override fun onBack() {
      navigator.goBack()
   }

   override fun goNext() {
      if (bluetoothService.isEnable && networkDelegate.isAvailable) {
         navigator.goForcePairKey()
      } else {
         view!!.showDialogEnableBleAndInternet()
      }
   }
}
