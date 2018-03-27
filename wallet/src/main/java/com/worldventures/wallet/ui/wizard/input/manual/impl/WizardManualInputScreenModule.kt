package com.worldventures.wallet.ui.wizard.input.manual.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.input.helper.InputAnalyticsDelegate
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegateImpl
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardManualInputScreenImpl::class), complete = false)
class WizardManualInputScreenModule {

   @Provides
   fun provideWizardManualInputPresenter(navigator: Navigator, deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                         analyticsInteractor: WalletAnalyticsInteractor, wizardInteractor: WizardInteractor,
                                         smartCardInteractor: SmartCardInteractor): WizardManualInputPresenter {
      return WizardManualInputPresenterImpl(navigator, deviceConnectionDelegate,
            analyticsInteractor, InputBarcodeDelegateImpl(navigator, wizardInteractor,
            InputAnalyticsDelegate.createForManualInputScreen(analyticsInteractor), smartCardInteractor))
   }
}
