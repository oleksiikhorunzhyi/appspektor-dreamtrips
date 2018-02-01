package com.worldventures.wallet.ui.records.swiping.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.records.swiping.WizardChargingPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardChargingScreenImpl::class), complete = false)
class WizardChargingScreenModule {

   @Provides
   fun providesWizardChargingPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       smartCardInteractor: SmartCardInteractor,
                                       analyticsInteractor: WalletAnalyticsInteractor): WizardChargingPresenter =
         WizardChargingPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor)

}
