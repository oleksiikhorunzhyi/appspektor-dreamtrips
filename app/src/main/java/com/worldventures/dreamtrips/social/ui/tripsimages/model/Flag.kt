package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable

data class Flag(val id: Int, val name: String, val isRequireDescription: Boolean) : Parcelable {

   constructor(source: Parcel) : this(
         source.readInt(),
         source.readString(),
         1 == source.readInt()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeInt(id)
      writeString(name)
      writeInt((if (isRequireDescription) 1 else 0))
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<Flag> = object : Parcelable.Creator<Flag> {
         override fun createFromParcel(source: Parcel): Flag = Flag(source)
         override fun newArray(size: Int): Array<Flag?> = arrayOfNulls(size)
      }
   }
}
