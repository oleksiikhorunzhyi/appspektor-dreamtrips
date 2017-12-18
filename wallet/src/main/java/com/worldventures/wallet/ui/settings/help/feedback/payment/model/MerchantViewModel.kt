package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import com.worldventures.core.utils.ProjectTextUtils.isNotEmpty
import com.worldventures.wallet.BR

private const val DEFAULT_SELECTED_POSITION = 0

data class MerchantViewModel(
      var selectedTypeIndex: Int = DEFAULT_SELECTED_POSITION,
      var merchantType: String? = null, //it's value of selectedTypeIndex
      private var _merchantName: String = "",
      private var _addressLine1: String = "",
      var addressLine2: String = "",
      var city: String = "",
      var state: String = "",
      var zip: String = ""
) : BaseObservable(), Parcelable {

   var merchantName: String
      @Bindable get() = _merchantName
      set(merchantName) {
         _merchantName = merchantName
         notifyPropertyChanged(BR.merchantName)
      }

   var addressLine1: String
      @Bindable get() = _addressLine1
      set(addressLine1) {
         _addressLine1 = addressLine1
         notifyPropertyChanged(BR.addressLine1)
      }

   val isDataChanged: Boolean
      get() = (selectedTypeIndex != DEFAULT_SELECTED_POSITION
            || isNotEmpty(merchantName)
            || isNotEmpty(addressLine1)
            || isNotEmpty(addressLine2)
            || isNotEmpty(city)
            || isNotEmpty(state)
            || isNotEmpty(zip))

   constructor(parcel: Parcel) : this(
         parcel.readInt(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString())

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeInt(selectedTypeIndex)
      parcel.writeString(merchantType)
      parcel.writeString(_merchantName)
      parcel.writeString(_addressLine1)
      parcel.writeString(addressLine2)
      parcel.writeString(city)
      parcel.writeString(state)
      parcel.writeString(zip)
   }

   override fun describeContents() = 0

   companion object CREATOR : Parcelable.Creator<MerchantViewModel> {
      override fun createFromParcel(parcel: Parcel) = MerchantViewModel(parcel)

      override fun newArray(size: Int): Array<MerchantViewModel?> = arrayOfNulls(size)
   }
}
