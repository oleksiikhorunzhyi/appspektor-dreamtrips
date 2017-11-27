package com.worldventures.wallet.ui.records.add

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.wallet.domain.entity.SDKCardType
import com.worldventures.wallet.domain.entity.SDKFinancialService

data class RecordBundle(
      val bankName: String,
      val version: String,
      val financialService: SDKFinancialService,
      val cardType: SDKCardType,
      val expDate: String,
      val cvv: String,
      val cardNumber: String,
      val t1: String,
      val t2: String,
      val t3: String,
      val firstName: String,
      val middleName: String,
      val lastName: String
) : Parcelable {

   constructor(parcel: Parcel) : this(
         parcel.readString(),
         parcel.readString(),
         SDKFinancialService.valueOf(parcel.readString()),
         SDKCardType.valueOf(parcel.readString()),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString(),
         parcel.readString())

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeString(bankName)
      parcel.writeString(version)
      parcel.writeString(financialService.name)
      parcel.writeString(cardType.name)
      parcel.writeString(expDate)
      parcel.writeString(cvv)
      parcel.writeString(cardNumber)
      parcel.writeString(t1)
      parcel.writeString(t2)
      parcel.writeString(t3)
      parcel.writeString(firstName)
      parcel.writeString(middleName)
      parcel.writeString(lastName)
   }

   override fun describeContents(): Int = 0

   companion object CREATOR : Parcelable.Creator<RecordBundle> {
      override fun createFromParcel(parcel: Parcel) = RecordBundle(parcel)

      override fun newArray(size: Int): Array<RecordBundle?> = arrayOfNulls(size)
   }
}