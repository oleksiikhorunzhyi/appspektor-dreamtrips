package com.worldventures.wallet.ui.settings.general.newcard.poweron.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateModule
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateFactory
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(NewCardPowerOnScreenImpl::class),
      includes = arrayOf(FactoryResetDelegateModule::class), complete = false)
class NewCardPowerOnScreenModule {

   @Provides
   fun provideNewCardPowerOnPresenter(navigator: Navigator,
                                      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                      smartCardInteractor: SmartCardInteractor,
                                      walletBluetoothService: WalletBluetoothService,
                                      factoryResetDelegateFactory: FactoryResetDelegateFactory): NewCardPowerOnPresenter {
      return NewCardPowerOnPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, walletBluetoothService,
            factoryResetDelegateFactory.createFactoryResetDelegate(FactoryResetType.NEW_CARD)) // todo is it NEW_CARD ??
   }
}
