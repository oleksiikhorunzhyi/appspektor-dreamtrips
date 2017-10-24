package com.worldventures.dreamtrips.social.domain.entity

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.core.ui.view.adapter.HeaderItem

data class InviteTemplate(val id: Int, val title: String, val coverUrl: String, val content: String?,
                          val category: String?, val link: String? = null) : Parcelable, HeaderItem {

   override fun getHeaderTitle() = title

   constructor(source: Parcel) : this(
         source.readInt(),
         source.readString(),
         source.readString(),
         source.readString(),
         source.readString(),
         source.readString()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeInt(id)
      writeString(title)
      writeString(coverUrl)
      writeString(content)
      writeString(category)
      writeString(link)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<InviteTemplate> = object : Parcelable.Creator<InviteTemplate> {
         override fun createFromParcel(source: Parcel) = InviteTemplate(source)
         override fun newArray(size: Int): Array<InviteTemplate?> = arrayOfNulls(size)
      }
   }
}