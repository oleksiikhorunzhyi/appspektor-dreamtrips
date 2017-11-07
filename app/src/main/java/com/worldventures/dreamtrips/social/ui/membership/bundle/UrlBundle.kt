package com.worldventures.dreamtrips.social.ui.membership.bundle

import android.os.Parcel
import android.os.Parcelable

data class UrlBundle(val url: String?) : Parcelable {

   constructor(source: Parcel) : this(
         source.readString()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(url)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<UrlBundle> = object : Parcelable.Creator<UrlBundle> {
         override fun createFromParcel(source: Parcel): UrlBundle = UrlBundle(source)
         override fun newArray(size: Int): Array<UrlBundle?> = arrayOfNulls(size)
      }
   }
}
