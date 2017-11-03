package com.worldventures.wallet.ui.settings.help.feedback.payment

import com.worldventures.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter

interface PaymentFeedbackPresenter : BaseSendFeedbackPresenter<PaymentFeedbackScreen> {

   fun discardChanges()

   fun showBackConfirmation()
}
