package com.worldventures.dreamtrips.social.service.profile.model

import android.os.Parcel
import android.os.Parcelable

class ReloadFeedModel() : Parcelable {

   var isVisible: Boolean = false

   protected constructor(parcel: Parcel) : this() {
      this.isVisible = parcel.readByte().toInt() != 0
   }

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeByte(if (isVisible) 1 else 0)
   }

   companion object {
      val CREATOR: Parcelable.Creator<ReloadFeedModel> = object : Parcelable.Creator<ReloadFeedModel> {
         override fun createFromParcel(source: Parcel) = ReloadFeedModel(source)
         override fun newArray(size: Int): Array<ReloadFeedModel?> = arrayOfNulls(size)
      }
   }
}
