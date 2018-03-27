package com.worldventures.wallet.ui.settings.general.newcard.check.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateModule
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateFactory
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(PreCheckNewCardScreenImpl::class),
      includes = arrayOf(FactoryResetDelegateModule::class), complete = false)
class PreCheckNewCardScreenModule {

   @Provides
   fun providePreCheckNewCardPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       smartCardInteractor: SmartCardInteractor,
                                       analyticsInteractor: WalletAnalyticsInteractor,
                                       walletBluetoothService: WalletBluetoothService,
                                       factoryResetDelegateFactory: FactoryResetDelegateFactory): PreCheckNewCardPresenter =
         PreCheckNewCardPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
               walletBluetoothService, factoryResetDelegateFactory.createFactoryResetDelegate(FactoryResetType.NEW_CARD))
}
