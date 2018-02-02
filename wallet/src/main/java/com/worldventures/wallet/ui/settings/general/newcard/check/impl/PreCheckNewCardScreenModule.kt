package com.worldventures.wallet.ui.settings.general.newcard.check.impl

import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(PreCheckNewCardScreenImpl::class), complete = false)
class PreCheckNewCardScreenModule {

   @Provides
   fun providePreCheckNewCardPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       smartCardInteractor: SmartCardInteractor,
                                       analyticsInteractor: WalletAnalyticsInteractor,
                                       factoryResetInteractor: FactoryResetInteractor,
                                       walletBluetoothService: WalletBluetoothService): PreCheckNewCardPresenter =
         PreCheckNewCardPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
               factoryResetInteractor, walletBluetoothService)
}
