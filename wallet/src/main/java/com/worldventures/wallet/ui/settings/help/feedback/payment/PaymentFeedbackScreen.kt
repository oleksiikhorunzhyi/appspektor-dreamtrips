package com.worldventures.wallet.ui.settings.help.feedback.payment

import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen
import com.worldventures.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel

import rx.Observable

interface PaymentFeedbackScreen : BaseFeedbackScreen {

   val paymentFeedbackViewModel: PaymentFeedbackViewModel

   fun observeMerchantName(): Observable<CharSequence>

   fun showBackConfirmDialog()

   fun discardViewModelChanges()
}
