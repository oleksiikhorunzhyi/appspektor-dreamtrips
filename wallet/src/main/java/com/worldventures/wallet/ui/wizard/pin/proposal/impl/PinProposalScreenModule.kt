package com.worldventures.wallet.ui.wizard.pin.proposal.impl

import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(PinProposalScreenImpl::class), complete = false)
class PinProposalScreenModule {

   @Provides
   fun providesPinProposalPresenter(navigator: Navigator,
                                    deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                    wizardInteractor: WizardInteractor): PinProposalPresenter =
         PinProposalPresenterImpl(navigator, deviceConnectionDelegate, wizardInteractor)
}
