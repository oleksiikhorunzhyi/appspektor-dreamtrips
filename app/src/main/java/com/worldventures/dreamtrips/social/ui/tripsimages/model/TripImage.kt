package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.core.model.ImagePathHolder
import com.worldventures.core.utils.ImageUtils

class TripImage() : Parcelable, ImagePathHolder {

   lateinit var id: String
   var description: String? = null
   var url: String = ""
   var type: String? = null
   var originUrl: String? = null

   fun getUrl(width: Int, height: Int): String = ImageUtils.getParametrizedUrl(url, width, height)

   override fun getImagePath(): String? = url

   constructor(source: Parcel) : this() {
      id = source.readString()
      description = source.readString()
      url = source.readString()
      type = source.readString()
      originUrl = source.readString()
   }

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(id)
      writeString(description)
      writeString(url)
      writeString(type)
      writeString(originUrl)
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
