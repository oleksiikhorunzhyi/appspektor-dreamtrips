package com.worldventures.wallet.ui.settings.help.feedback.payment.impl

import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutableMerchant
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutablePaymentFeedback
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutablePaymentTerminal
import com.worldventures.dreamtrips.api.smart_card.feedback.model.Merchant
import com.worldventures.dreamtrips.api.smart_card.feedback.model.PaymentTerminal
import com.worldventures.wallet.ui.settings.help.feedback.payment.model.MerchantViewModel
import com.worldventures.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel
import com.worldventures.wallet.ui.settings.help.feedback.payment.model.PaymentTerminalViewModel

class PaymentFeedbackConverter {

   fun createFeedback(paymentFeedbackViewModel: PaymentFeedbackViewModel): ImmutablePaymentFeedback.Builder {
      return ImmutablePaymentFeedback.builder()
            .paymentSucceeded(paymentFeedbackViewModel.attemptsView.isSuccessPayment)
            .countAttempts(paymentFeedbackViewModel.attemptsView.countOfAttempts)
            .merchant(fetchMerchant(paymentFeedbackViewModel.merchantView))
            .paymentTerminal(fetchPaymentTerminalData(paymentFeedbackViewModel.terminalView))
            .notes(paymentFeedbackViewModel.infoView.notes)
   }

   private fun fetchPaymentTerminalData(terminalView: PaymentTerminalViewModel): PaymentTerminal {
      return ImmutablePaymentTerminal.builder()
            .nameAndModel(terminalView.terminalNameModel)
            .paymentType(terminalView.paymentType)
            .build()
   }

   private fun fetchMerchant(merchantView: MerchantViewModel): Merchant {
      return ImmutableMerchant.builder()
            .address1(merchantView.addressLine1)
            .address2(merchantView.addressLine2)
            .name(merchantView.merchantName)
            .city(merchantView.city)
            .state(merchantView.state)
            .zip(merchantView.zip)
            .type(merchantView.merchantType)
            .build()
   }
}
