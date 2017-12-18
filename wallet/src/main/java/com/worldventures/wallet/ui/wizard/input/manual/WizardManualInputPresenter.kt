package com.worldventures.wallet.ui.wizard.input.manual

import com.worldventures.wallet.ui.common.base.WalletPresenter
import com.worldventures.wallet.ui.wizard.input.helper.BaseBarcodeInputPresenter

interface WizardManualInputPresenter : WalletPresenter<WizardManualInputScreen>, BaseBarcodeInputPresenter {

   fun goBack()

}
