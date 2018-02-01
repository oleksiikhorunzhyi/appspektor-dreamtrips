package com.worldventures.wallet.ui.records.detail

import android.os.Parcel
import android.os.Parcelable

data class DefaultRecordDetail(val recordId: String, val recordName: String) : Parcelable {

   constructor(source: Parcel) : this(
         source.readString(),
         source.readString()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(recordId)
      writeString(recordName)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<DefaultRecordDetail> = object : Parcelable.Creator<DefaultRecordDetail> {
         override fun createFromParcel(source: Parcel) = DefaultRecordDetail(source)
         override fun newArray(size: Int): Array<DefaultRecordDetail?> = arrayOfNulls(size)
      }
   }
}
