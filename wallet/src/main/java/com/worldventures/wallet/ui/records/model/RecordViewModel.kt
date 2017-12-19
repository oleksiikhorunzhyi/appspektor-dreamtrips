package com.worldventures.wallet.ui.records.model

import android.os.Parcel
import android.os.Parcelable

import com.worldventures.wallet.domain.entity.record.RecordType

class RecordViewModel(
      val id: String? = null,
      val cvvLength: Int,
      val nickName: String,
      val ownerName: String,
      val cardNumber: String,
      val expireDate: String,
      val recordType: RecordType
) : Parcelable {

   private constructor(`in`: Parcel) : this(
         id = `in`.readString(),
         cvvLength = `in`.readInt(),
         nickName = `in`.readString(),
         ownerName = `in`.readString(),
         cardNumber = `in`.readString(),
         expireDate = `in`.readString(),
         recordType = `in`.readSerializable() as RecordType)

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeString(id)
      dest.writeInt(cvvLength)
      dest.writeString(nickName)
      dest.writeString(ownerName)
      dest.writeString(cardNumber)
      dest.writeString(expireDate)
      dest.writeSerializable(recordType)
   }

   companion object CREATOR : Parcelable.Creator<RecordViewModel> {
      override fun createFromParcel(parcel: Parcel) = RecordViewModel(parcel)

      override fun newArray(size: Int): Array<RecordViewModel?> = arrayOfNulls(size)
   }
}
