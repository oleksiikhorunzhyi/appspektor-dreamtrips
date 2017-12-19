package com.worldventures.wallet.ui.wizard.power_on.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardPowerOnScreenImpl::class), complete = false)
class WizardPowerOnScreenModule {

   @Provides
   fun provideWizardPowerOnPresenter(navigator: Navigator,
                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                     networkDelegate: WalletNetworkDelegate,
                                     walletBluetoothService: WalletBluetoothService,
                                     analyticsInteractor: WalletAnalyticsInteractor): WizardPowerOnPresenter =
         WizardPowerOnPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate, walletBluetoothService, analyticsInteractor)

}
