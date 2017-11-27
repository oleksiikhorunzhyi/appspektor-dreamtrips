package com.worldventures.wallet.ui.wizard.power_on

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WizardPowerOnPresenter : WalletPresenter<WizardPowerOnScreen> {

   fun onBack()

   fun onNext()
}
