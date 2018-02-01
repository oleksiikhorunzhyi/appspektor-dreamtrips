package com.worldventures.wallet.ui.settings.general.newcard.poweron.impl

import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(NewCardPowerOnScreenImpl::class), complete = false)
class NewCardPowerOnScreenModule {

   @Provides
   fun provideNewCardPowerOnPresenter(navigator: Navigator,
                                      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                      smartCardInteractor: SmartCardInteractor,
                                      factoryResetInteractor: FactoryResetInteractor,
                                      analyticsInteractor: WalletAnalyticsInteractor,
                                      walletBluetoothService: WalletBluetoothService): NewCardPowerOnPresenter {
      return NewCardPowerOnPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            factoryResetInteractor, analyticsInteractor, walletBluetoothService)
   }
}
