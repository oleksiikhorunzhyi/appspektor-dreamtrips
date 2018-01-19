package com.worldventures.wallet.ui.wizard.assign

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WizardAssignUserPresenter : WalletPresenter<WizardAssignUserScreen> {

   fun onWizardComplete()

   fun retryUploadDummyCards()

   fun cancelUploadDummyCards()

   fun onWizardCancel()
}
