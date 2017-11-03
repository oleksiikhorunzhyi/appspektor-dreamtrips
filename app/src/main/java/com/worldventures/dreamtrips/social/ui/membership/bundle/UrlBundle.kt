package com.worldventures.dreamtrips.social.ui.membership.bundle

import android.os.Parcel
import android.os.Parcelable

class UrlBundle (val url: String?) : Parcelable {

   constructor(`in`: Parcel) : this(`in`.readString())

   override fun writeToParcel(parcel: Parcel, i: Int) {
      parcel.writeString(url)
   }

   override fun describeContents() = 0

   companion object {

      val CREATOR: Parcelable.Creator<UrlBundle> = object : Parcelable.Creator<UrlBundle> {
         override fun createFromParcel(`in`: Parcel) = UrlBundle(`in`)

         override fun newArray(size: Int): Array<UrlBundle?> = arrayOfNulls(size)
      }
   }
}
