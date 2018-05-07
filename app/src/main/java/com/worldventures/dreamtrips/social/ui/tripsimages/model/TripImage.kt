package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.core.model.ImagePathHolder

class TripImage() : Parcelable, ImagePathHolder {

   var url = ""

   constructor(url: String): this() {
      this.url = url
   }

   constructor(source: Parcel) : this() {
      url = source.readString()
   }

   override fun getImagePath() = url

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(url)
   }

   companion object {
      val serialVersionUID = 128L

      @JvmField
      val CREATOR: Parcelable.Creator<TripImage> = object : Parcelable.Creator<TripImage> {
         override fun createFromParcel(source: Parcel): TripImage = TripImage(source)
         override fun newArray(size: Int): Array<TripImage?> = arrayOfNulls(size)
      }
   }
}
