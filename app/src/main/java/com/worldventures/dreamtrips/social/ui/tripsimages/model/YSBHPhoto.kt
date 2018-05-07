package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable

class YSBHPhoto() : Parcelable {

   var id: Int = 0
   lateinit var url: String
   lateinit var title: String

   constructor(id: Int, url: String, title: String) : this() {
      this.id = id
      this.url = url
      this.title = title
   }

   constructor(source: Parcel) : this(
         source.readInt(),
         source.readString(),
         source.readString()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeInt(id)
      writeString(url)
      writeString(title)
   }

   @SuppressWarnings("UnsafeCast")
   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as YSBHPhoto

      if (id != other.id) return false

      return true
   }

   override fun hashCode(): Int {
      return id
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<YSBHPhoto> = object : Parcelable.Creator<YSBHPhoto> {
         override fun createFromParcel(source: Parcel): YSBHPhoto = YSBHPhoto(source)
         override fun newArray(size: Int): Array<YSBHPhoto?> = arrayOfNulls(size)
      }
   }
}
