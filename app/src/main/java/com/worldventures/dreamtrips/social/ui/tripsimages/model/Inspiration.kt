package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable

class Inspiration() : Parcelable {

   lateinit var id: String
   lateinit var url : String
   lateinit var quote : String
   lateinit var author: String

   constructor(id : String, url : String, quote : String, author : String) : this() {
      this.id = id
      this.url = url
      this.quote = quote
      this.author = author
   }

   constructor(source: Parcel) : this(
         source.readString(),
         source.readString(),
         source.readString(),
         source.readString()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(id)
      writeString(url)
      writeString(quote)
      writeString(author)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<Inspiration> = object : Parcelable.Creator<Inspiration> {
         override fun createFromParcel(source: Parcel): Inspiration = Inspiration(source)
         override fun newArray(size: Int): Array<Inspiration?> = arrayOfNulls(size)
      }
   }
}
