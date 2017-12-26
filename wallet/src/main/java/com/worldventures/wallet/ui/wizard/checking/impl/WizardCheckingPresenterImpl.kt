package com.worldventures.wallet.ui.wizard.checking.impl

import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingPresenter
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingScreen
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

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
                  .startWith(bluetoothService.isEnable),
            networkDelegate.observeConnectedState()
                  .startWith(networkDelegate.isAvailable),
            { bluetooth, internet -> Pair<Boolean, Boolean>(bluetooth, internet)})
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pair ->
               view.buttonEnable(pair.first && pair.second)
               view.bluetoothEnable(pair.first)
               view.networkAvailable(pair.second)
            }, {t -> Timber.e(t) })
   }
}
