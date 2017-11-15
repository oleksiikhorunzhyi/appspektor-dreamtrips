package com.worldventures.dreamtrips.social.ui.reptools.model

import android.os.Parcel
import android.os.Parcelable

import com.worldventures.core.model.BaseEntity
import com.worldventures.core.model.Filterable
import com.worldventures.core.ui.view.adapter.HeaderItem

class SuccessStory(id: Int,
                   val author: String,
                   val category: String,
                   val locale: String,
                   val url: String,
                   val sharingUrl: String,
                   var isLiked: Boolean)
                              : BaseEntity(id), Parcelable, Filterable, HeaderItem {
   var isSelected: Boolean = false

   override fun containsQuery(query: String?): Boolean {
      return query == null || author.toLowerCase().contains(query)
   }

   override fun getHeaderTitle(): String? = category

   constructor(source: Parcel) : this(
         source.readInt(),
         source.readString(),
         source.readString(),
         source.readString(),
         source.readString(),
         source.readString(),
         1 == source.readInt()
   ) {
      isSelected = 1 == source.readInt()
   }

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeInt(id)
      writeString(author)
      writeString(category)
      writeString(locale)
      writeString(url)
      writeString(sharingUrl)
      writeInt((if (isLiked) 1 else 0))
      writeInt((if (isSelected) 1 else 0))
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<SuccessStory> = object : Parcelable.Creator<SuccessStory> {
         override fun createFromParcel(source: Parcel): SuccessStory = SuccessStory(source)
         override fun newArray(size: Int): Array<SuccessStory?> = arrayOfNulls(size)
      }
   }
}
