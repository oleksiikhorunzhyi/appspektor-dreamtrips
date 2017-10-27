package com.worldventures.wallet.ui.wizard.input.scanner

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WizardScanBarcodePresenter : WalletPresenter<WizardScanBarcodeScreen> {

   fun goBack()

   fun requestCamera()

   fun barcodeScanned(barcode: String)

   fun startManualInput()

   fun retry(barcode: String)

   fun retryAssignedToCurrentDevice()

   fun retryScan()
}
