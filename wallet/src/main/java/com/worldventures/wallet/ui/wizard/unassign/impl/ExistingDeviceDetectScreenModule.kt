package com.worldventures.wallet.ui.wizard.unassign.impl

import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(ExistingDeviceDetectScreenImpl::class), complete = false)
class ExistingDeviceDetectScreenModule {

   @Provides
   fun provideExistingDeviceDetectPresenter(navigator: Navigator,
                                            deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                            wizardInteractor: WizardInteractor,
                                            httpErrorHandlingUtil: HttpErrorHandlingUtil): ExistingDeviceDetectPresenter {
      return ExistingDeviceDetectPresenterImpl(navigator, deviceConnectionDelegate,
            wizardInteractor, httpErrorHandlingUtil)
   }
}
