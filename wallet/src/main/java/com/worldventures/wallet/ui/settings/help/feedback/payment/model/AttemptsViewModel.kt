package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable

import com.worldventures.wallet.BR

class AttemptsViewModel : BaseObservable() {

   var isSuccessPayment = true
   @get:Bindable
   var countOfAttempts = 1
      set(countOfAttempts) {
         field = countOfAttempts
         notifyPropertyChanged(BR.countOfAttempts)
      }

   val isDataChanged: Boolean
      get() = this.countOfAttempts != 1 || !isSuccessPayment

   override fun equals(other: Any?): Boolean {
      if (this === other) {
         return true
      }
      if (other == null || javaClass != other.javaClass) {
         return false
      }

      val that = other as AttemptsViewModel?

      return if (isSuccessPayment != that!!.isSuccessPayment) {
         false
      } else this.countOfAttempts == that.countOfAttempts

   }

   override fun hashCode(): Int {
      var result = if (isSuccessPayment) 1 else 0
      result = 31 * result + this.countOfAttempts
      return result
   }
}
