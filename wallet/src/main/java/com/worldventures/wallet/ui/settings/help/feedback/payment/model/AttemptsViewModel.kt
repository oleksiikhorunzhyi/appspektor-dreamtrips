package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable

import com.worldventures.wallet.BR

private const val DEFAULT_ATTEMPTS_COUNT = 1

data class AttemptsViewModel(
      var isSuccessPayment: Boolean = true,
      private var _countOfAttempts: Int = DEFAULT_ATTEMPTS_COUNT) : BaseObservable() {

   var countOfAttempts
      @Bindable get() = _countOfAttempts
      set(countOfAttempts) {
         _countOfAttempts = countOfAttempts
         notifyPropertyChanged(BR.countOfAttempts)
      }

   val isDataChanged
      get() = this.countOfAttempts != DEFAULT_ATTEMPTS_COUNT || !isSuccessPayment
}
