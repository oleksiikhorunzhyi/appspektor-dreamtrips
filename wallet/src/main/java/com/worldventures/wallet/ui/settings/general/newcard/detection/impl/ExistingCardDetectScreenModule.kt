package com.worldventures.wallet.ui.settings.general.newcard.detection.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateModule
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateFactory
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(ExistingCardDetectScreenImpl::class),
      includes = arrayOf(FactoryResetDelegateModule::class), complete = false)
class ExistingCardDetectScreenModule {

   @Provides
   fun provideExistingCardDetectPresenter(navigator: Navigator,
                                          deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                          smartCardInteractor: SmartCardInteractor,
                                          analyticsInteractor: WalletAnalyticsInteractor,
                                          factoryResetDelegateFactory: FactoryResetDelegateFactory): ExistingCardDetectPresenter =
         ExistingCardDetectPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
               factoryResetDelegateFactory.createFactoryResetDelegate(FactoryResetType.NEW_CARD))
}
