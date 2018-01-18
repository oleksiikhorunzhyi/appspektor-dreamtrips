package com.worldventures.wallet.ui.wizard.input.helper

import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus

interface BaseBarcodeInputPresenter {

   fun checkBarcode(barcode: String)

   fun retry(barcode: String)

   fun retryAssignedToCurrentDevice()

   fun retryAgreementsAccept(smartCardStatus: SmartCardStatus, smartCardId: String)

   fun handleAcceptanceCancelled()
}
