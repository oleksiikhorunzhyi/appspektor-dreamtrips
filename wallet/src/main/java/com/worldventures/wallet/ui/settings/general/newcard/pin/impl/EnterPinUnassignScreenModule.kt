package com.worldventures.wallet.ui.settings.general.newcard.pin.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateModule
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateFactory
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(EnterPinUnassignScreenImpl::class),
      includes = arrayOf(FactoryResetDelegateModule::class), complete = false)
class EnterPinUnassignScreenModule {

   @Provides
   fun provideEnterPinUnassignPresenter(navigator: Navigator,
                                        deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                        analyticsInteractor: WalletAnalyticsInteractor,
                                        factoryResetDelegateFactory: FactoryResetDelegateFactory): EnterPinUnassignPresenter =
         EnterPinUnassignPresenterImpl(navigator, deviceConnectionDelegate,
               factoryResetDelegateFactory.createFactoryResetDelegate(FactoryResetType.NEW_CARD_ENTER_PIN), analyticsInteractor)
}
