package com.worldventures.wallet.ui.wizard.input.helper

interface BaseBarcodeInputPresenter {

   fun checkBarcode(barcode: String)

   fun retry(barcode: String)

   fun retryAssignedToCurrentDevice()
}