package com.worldventures.dreamtrips.social.ui.membership.bundle

import android.os.Parcel
import android.os.Parcelable

data class ShareBundle(val shareLink: String? = null, val emailSubject: String? = null) : Parcelable {

   constructor(source: Parcel) : this(
         source.readString(),
         source.readString()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(shareLink)
      writeString(emailSubject)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<ShareBundle> = object : Parcelable.Creator<ShareBundle> {
         override fun createFromParcel(source: Parcel): ShareBundle = ShareBundle(source)
         override fun newArray(size: Int): Array<ShareBundle?> = arrayOfNulls(size)
      }
   }
}
