package com.worldventures.wallet.ui.wizard.pairkey.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(PairKeyScreenImpl::class), complete = false)
class PairKeyScreenModule {

   @Provides
   fun providePairKeyPresenter(navigator: Navigator,
                               deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                               smartCardInteractor: SmartCardInteractor,
                               wizardInteractor: WizardInteractor,
                               analyticsInteractor: WalletAnalyticsInteractor): PairKeyPresenter =
         PairKeyPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, wizardInteractor, analyticsInteractor)

}
