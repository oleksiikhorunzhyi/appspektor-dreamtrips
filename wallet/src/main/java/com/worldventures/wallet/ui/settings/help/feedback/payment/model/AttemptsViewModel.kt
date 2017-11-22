package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable

import com.worldventures.wallet.BR

private const val DEFAULT_ATTEMPTS_COUNT = 1

data class AttemptsViewModel(
      var isSuccessPayment: Boolean = true,
      private var _countOfAttempts: Int = DEFAULT_ATTEMPTS_COUNT) : BaseObservable(), Parcelable {

   var countOfAttempts
      @Bindable get() = _countOfAttempts
      set(countOfAttempts) {
         _countOfAttempts = countOfAttempts
         notifyPropertyChanged(BR.countOfAttempts)
      }

   val isDataChanged
      get() = this.countOfAttempts != DEFAULT_ATTEMPTS_COUNT || !isSuccessPayment

   constructor(parcel: Parcel) : this(
         parcel.readByte() != 0.toByte(),
         parcel.readInt())

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeByte(if (isSuccessPayment) 1 else 0)
      parcel.writeInt(_countOfAttempts)
   }

   override fun describeContents() = 0

   companion object CREATOR : Parcelable.Creator<AttemptsViewModel> {
      override fun createFromParcel(parcel: Parcel) = AttemptsViewModel(parcel)

      override fun newArray(size: Int): Array<AttemptsViewModel?> = arrayOfNulls(size)
   }
}
