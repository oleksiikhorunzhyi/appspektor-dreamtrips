package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import com.worldventures.wallet.BR
import com.worldventures.wallet.domain.entity.settings.payment_feedback.PaymentType

data class PaymentTerminalViewModel(private var _terminalNameModel: String = "") : BaseObservable(), Parcelable {

   var terminalNameModel: String
      @Bindable get() = _terminalNameModel
      set(terminalNameModel) {
         _terminalNameModel = terminalNameModel
         notifyPropertyChanged(BR.terminalNameModel)
      }

   val paymentType: String
      get() = PaymentType.WIRELESS_MAGNETIC_SWIPE.type()

   val isDataChanged: Boolean
      get() = this.terminalNameModel.isNotEmpty()

   constructor(parcel: Parcel) : this(parcel.readString())

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeString(_terminalNameModel)
   }

   override fun describeContents(): Int = 0

   companion object CREATOR : Parcelable.Creator<PaymentTerminalViewModel> {
      override fun createFromParcel(parcel: Parcel) = PaymentTerminalViewModel(parcel)

      override fun newArray(size: Int): Array<PaymentTerminalViewModel?> = arrayOfNulls(size)
   }
}
