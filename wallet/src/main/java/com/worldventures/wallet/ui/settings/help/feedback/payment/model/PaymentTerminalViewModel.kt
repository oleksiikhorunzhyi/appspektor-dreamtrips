package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.worldventures.wallet.BR
import com.worldventures.wallet.domain.entity.settings.payment_feedback.PaymentType

class PaymentTerminalViewModel : BaseObservable() {

   @get:Bindable
   var terminalNameModel: String = ""
      set(terminalNameModel) {
         field = terminalNameModel
         notifyPropertyChanged(BR.terminalNameModel)
      }

   val paymentType: String
      get() = PaymentType.WIRELESS_MAGNETIC_SWIPE.type()

   val isDataChanged: Boolean
      get() = this.terminalNameModel.isNotEmpty()

   override fun equals(other: Any?): Boolean {
      if (this === other) {
         return true
      }
      if (other == null || javaClass != other.javaClass) {
         return false
      }

      val that = other as PaymentTerminalViewModel

      return this.terminalNameModel == that.terminalNameModel

   }

   override fun hashCode() = terminalNameModel.hashCode()
}
