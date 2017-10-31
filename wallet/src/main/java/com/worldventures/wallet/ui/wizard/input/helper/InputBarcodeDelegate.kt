package com.worldventures.wallet.ui.wizard.input.helper

interface InputBarcodeDelegate {

   fun barcodeEntered(barcode: String)

   fun retry(barcode: String)

   fun init(inputDelegateView: InputDelegateView)

   fun retryAssignedToCurrentDevice()
}
