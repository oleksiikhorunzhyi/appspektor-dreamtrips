package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import com.worldventures.wallet.BR

data class AdditionalInfoViewModel(private var _notes: String = "") : BaseObservable(), Parcelable {

   var notes: String
      @Bindable get() = _notes
      set(notes) {
         _notes = notes
         notifyPropertyChanged(BR.notes)
      }

   val isDataChanged: Boolean
      get() = this.notes.isNotEmpty()

   constructor(parcel: Parcel) : this(parcel.readString())

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeString(_notes)
   }

   override fun describeContents() = 0

   companion object CREATOR : Parcelable.Creator<AdditionalInfoViewModel> {
      override fun createFromParcel(parcel: Parcel) = AdditionalInfoViewModel(parcel)

      override fun newArray(size: Int): Array<AdditionalInfoViewModel?> = arrayOfNulls(size)
   }
}
