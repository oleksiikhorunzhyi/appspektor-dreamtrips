package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.os.Parcel
import android.os.Parcelable

data class PaymentFeedbackViewModel(
      val attemptsView: AttemptsViewModel = AttemptsViewModel(),
      val merchantView: MerchantViewModel = MerchantViewModel(),
      val terminalView: PaymentTerminalViewModel = PaymentTerminalViewModel(),
      val infoView: AdditionalInfoViewModel = AdditionalInfoViewModel()
) : BaseObservable(), Parcelable {

   private var canBeLost: Boolean = false

   val isDataChanged: Boolean
      get() = !canBeLost && (this.attemptsView.isDataChanged
            || this.merchantView.isDataChanged
            || this.terminalView.isDataChanged
            || this.infoView.isDataChanged)

   constructor(parcel: Parcel) : this(
         parcel.readParcelable(AttemptsViewModel::class.java.classLoader),
         parcel.readParcelable(MerchantViewModel::class.java.classLoader),
         parcel.readParcelable(PaymentTerminalViewModel::class.java.classLoader),
         parcel.readParcelable(AdditionalInfoViewModel::class.java.classLoader)) {
      canBeLost = parcel.readByte() != 0.toByte()
   }

   fun setCanBeLost(canBeLost: Boolean) {
      this.canBeLost = canBeLost
   }

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeParcelable(attemptsView, flags)
      parcel.writeParcelable(merchantView, flags)
      parcel.writeParcelable(terminalView, flags)
      parcel.writeParcelable(infoView, flags)
      parcel.writeByte(if (canBeLost) 1 else 0)
   }

   override fun describeContents(): Int = 0

   companion object CREATOR : Parcelable.Creator<PaymentFeedbackViewModel> {
      override fun createFromParcel(parcel: Parcel) = PaymentFeedbackViewModel(parcel)

      override fun newArray(size: Int): Array<PaymentFeedbackViewModel?> = arrayOfNulls(size)
   }
}
