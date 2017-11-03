package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable

class PaymentFeedbackViewModel : BaseObservable() {

   val attemptsView: AttemptsViewModel = AttemptsViewModel()
   val merchantView: MerchantViewModel = MerchantViewModel()
   val terminalView: PaymentTerminalViewModel = PaymentTerminalViewModel()
   val infoView: AdditionalInfoViewModel = AdditionalInfoViewModel()

   private var canBeLost: Boolean = false

   val isDataChanged: Boolean
      get() = !canBeLost && (this.attemptsView.isDataChanged
            || this.merchantView.isDataChanged
            || this.terminalView.isDataChanged
            || this.infoView.isDataChanged)

   override fun equals(other: Any?): Boolean {
      if (this === other) {
         return true
      }
      if (other == null || javaClass != other.javaClass) {
         return false
      }

      val that = other as PaymentFeedbackViewModel

      if (this.attemptsView != that.attemptsView) {
         return false
      }
      if (this.merchantView != that.merchantView) {
         return false
      }
      if (this.terminalView != that.terminalView) {
         return false
      }
      return this.infoView == that.infoView

   }

   override fun hashCode(): Int {
      var result = this.attemptsView.hashCode()
      result = 31 * result + this.merchantView.hashCode()
      result = 31 * result + this.terminalView.hashCode()
      result = 31 * result + this.infoView.hashCode()
      return result
   }

   fun setCanBeLost(canBeLost: Boolean) {
      this.canBeLost = canBeLost
   }
}
