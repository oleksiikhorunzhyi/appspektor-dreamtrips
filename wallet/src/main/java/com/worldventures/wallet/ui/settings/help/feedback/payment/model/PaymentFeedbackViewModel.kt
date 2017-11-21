package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable

data class PaymentFeedbackViewModel(
      val attemptsView: AttemptsViewModel = AttemptsViewModel(),
      val merchantView: MerchantViewModel = MerchantViewModel(),
      val terminalView: PaymentTerminalViewModel = PaymentTerminalViewModel(),
      val infoView: AdditionalInfoViewModel = AdditionalInfoViewModel()
) : BaseObservable() {

   private var canBeLost: Boolean = false

   val isDataChanged: Boolean
      get() = !canBeLost && (this.attemptsView.isDataChanged
            || this.merchantView.isDataChanged
            || this.terminalView.isDataChanged
            || this.infoView.isDataChanged)

   fun setCanBeLost(canBeLost: Boolean) {
      this.canBeLost = canBeLost
   }
}
