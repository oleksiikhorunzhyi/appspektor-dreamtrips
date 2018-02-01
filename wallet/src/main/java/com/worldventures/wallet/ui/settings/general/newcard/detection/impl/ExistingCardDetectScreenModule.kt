package com.worldventures.wallet.ui.settings.general.newcard.detection.impl

import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(ExistingCardDetectScreenImpl::class), complete = false)
class ExistingCardDetectScreenModule {

   @Provides
   fun provideExistingCardDetectPresenter(navigator: Navigator,
                                          deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                          smartCardInteractor: SmartCardInteractor,
                                          analyticsInteractor: WalletAnalyticsInteractor,
                                          factoryResetInteractor: FactoryResetInteractor,
                                          httpErrorHandlingUtil: HttpErrorHandlingUtil): ExistingCardDetectPresenter =
         ExistingCardDetectPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
               factoryResetInteractor, httpErrorHandlingUtil)
}
