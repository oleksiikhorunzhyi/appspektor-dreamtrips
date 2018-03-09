package com.worldventures.wallet.ui.settings.general.reset.impl

import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateModule
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateFactory
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetPresenter
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(FactoryResetScreenImpl::class),
      includes = arrayOf(FactoryResetDelegateModule::class), complete = false)
class FactoryResetScreenModule {

   @Provides
   fun provideFactoryResetPresenter(navigator: Navigator,
                                    deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                    factoryResetDelegateFactory: FactoryResetDelegateFactory): FactoryResetPresenter =
         FactoryResetPresenterImpl(navigator, deviceConnectionDelegate,
               factoryResetDelegateFactory.createFactoryResetDelegate(FactoryResetType.SETTINGS_ENTER_PIN))
}
