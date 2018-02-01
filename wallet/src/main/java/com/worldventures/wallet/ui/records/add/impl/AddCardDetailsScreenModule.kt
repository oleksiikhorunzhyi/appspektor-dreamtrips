package com.worldventures.wallet.ui.records.add.impl

import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.records.add.AddCardDetailsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(AddCardDetailsScreenImpl::class), complete = false)
class AddCardDetailsScreenModule {

   @Provides
   fun provideAddCardDetailsPresenter(navigator: Navigator,
                                      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                      smartCardInteractor: SmartCardInteractor,
                                      analyticsInteractor: WalletAnalyticsInteractor,
                                      recordInteractor: RecordInteractor,
                                      wizardInteractor: WizardInteractor): AddCardDetailsPresenter =
         AddCardDetailsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
               recordInteractor, wizardInteractor)

}
