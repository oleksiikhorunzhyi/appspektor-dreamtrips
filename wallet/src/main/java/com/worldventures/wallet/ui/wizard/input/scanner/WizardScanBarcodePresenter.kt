package com.worldventures.wallet.ui.wizard.input.scanner

import com.worldventures.wallet.ui.common.base.WalletPresenter
import com.worldventures.wallet.ui.wizard.input.helper.BaseBarcodeInputPresenter

interface WizardScanBarcodePresenter : WalletPresenter<WizardScanBarcodeScreen>, BaseBarcodeInputPresenter {

   fun goBack()

   fun requestCamera()

   fun startManualInput()

   fun retryScan()
}
