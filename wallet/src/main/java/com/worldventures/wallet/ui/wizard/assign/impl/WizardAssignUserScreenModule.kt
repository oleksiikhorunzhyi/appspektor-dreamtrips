package com.worldventures.wallet.ui.wizard.assign.impl

import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserPresenter
import com.worldventures.wallet.util.WalletFeatureHelper
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardAssignUserScreenImpl::class), complete = false)
class WizardAssignUserScreenModule {

   @Provides
   fun provideWizardAssignUserPresenter(navigator: Navigator, deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                        smartCardInteractor: SmartCardInteractor, wizardInteractor: WizardInteractor,
                                        recordInteractor: RecordInteractor, analyticsInteractor: WalletAnalyticsInteractor,
                                        walletFeatureHelper: WalletFeatureHelper): WizardAssignUserPresenter {
      return WizardAssignUserPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            wizardInteractor, recordInteractor, analyticsInteractor, walletFeatureHelper)
   }
}
