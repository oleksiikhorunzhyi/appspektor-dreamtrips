package com.worldventures.wallet.ui.wizard.power_on.impl

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.PowerOnAction
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnPresenter
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnScreen

class WizardPowerOnPresenterImpl(navigator: Navigator,
                                 deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                 private val networkDelegate: WalletNetworkDelegate,
                                 private val bluetoothService: WalletBluetoothService,
                                 private val analyticsInteractor: WalletAnalyticsInteractor)
   : WalletPresenterImpl<WizardPowerOnScreen>(navigator, deviceConnectionDelegate), WizardPowerOnPresenter {

   override fun attachView(view: WizardPowerOnScreen) {
      super.attachView(view)
      networkDelegate.setup(view)

      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(PowerOnAction()))
   }

   override fun onBack() {
      navigator.goBack()
   }

   override fun onNext() {
      if (bluetoothService.isEnable && networkDelegate.isAvailable) {
         navigator.goWizardAffidavit()
      } else {
         navigator.goWizardChecks()
      }
   }
}
