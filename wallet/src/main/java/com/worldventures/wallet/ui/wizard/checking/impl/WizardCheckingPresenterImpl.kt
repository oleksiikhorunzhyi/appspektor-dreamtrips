package com.worldventures.wallet.ui.wizard.checking.impl

import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingPresenter
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingScreen
import rx.Observable

class WizardCheckingPresenterImpl(navigator: Navigator,
                                  deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                  private val networkDelegate: WalletNetworkDelegate,
                                  private val bluetoothService: WalletBluetoothService)
   : WalletPresenterImpl<WizardCheckingScreen>(navigator, deviceConnectionDelegate), WizardCheckingPresenter {

   override fun attachView(view: WizardCheckingScreen) {
      super.attachView(view)
      networkDelegate.setup(view)

      checkBleSupport(view)
      observeBluetoothAndNetwork(view)
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun goNext() {
      navigator.goWizardTerms()
   }

   private fun checkBleSupport(view: WizardCheckingScreen) {
      if (!bluetoothService.isSupported) {
         view.bluetoothDoesNotSupported()
      }
   }

   private fun observeBluetoothAndNetwork(view: WizardCheckingScreen) {
      Observable.combineLatest(
            bluetoothService.observeEnablesState()
                  .startWith(bluetoothService.isEnable)
                  .doOnNext { value -> view.bluetoothEnable(value) },
            networkDelegate.observeConnectedState()
                  .startWith(networkDelegate.isAvailable)
                  .doOnNext { value -> view.networkAvailable(value) },
            { bluetooth, internet ->
               view.buttonEnable(bluetooth and internet)
               true
            })
            .compose(view.bindUntilDetach())
            .subscribe()
   }
}
