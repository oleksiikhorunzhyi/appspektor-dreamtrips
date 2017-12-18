package com.worldventures.wallet.ui.wizard.checking

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WizardCheckingPresenter : WalletPresenter<WizardCheckingScreen> {

   fun goBack()

   fun goNext()
}
