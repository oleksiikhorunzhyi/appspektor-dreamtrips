package com.worldventures.wallet.ui.wizard.checking.impl

import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardCheckingScreenImpl::class), complete = false)
class WizardCheckingScreenModule {

   @Provides
   fun providesWizardCheckingPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       networkDelegate: WalletNetworkDelegate,
                                       walletBluetoothService: WalletBluetoothService): WizardCheckingPresenter =
         WizardCheckingPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate, walletBluetoothService)
}
