package com.worldventures.wallet.ui.wizard.input.manual

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WizardManualInputPresenter : WalletPresenter<WizardManualInputScreen> {

   fun goBack()

   fun checkBarcode(barcode: String)

   fun retry(barcode: String)

   fun retryAssignedToCurrentDevice()
}
