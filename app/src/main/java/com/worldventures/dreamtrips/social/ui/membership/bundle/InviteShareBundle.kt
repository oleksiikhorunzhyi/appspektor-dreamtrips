package com.worldventures.dreamtrips.social.ui.membership.bundle

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.dreamtrips.social.domain.entity.InviteType

data class InviteShareBundle(val type: InviteType, val shareText: String? = null, val emailSubject: String? = null) : Parcelable {

   constructor(source: Parcel) : this(
         InviteType.values()[source.readInt()],
         source.readString(),
         source.readString()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeInt(type.ordinal)
      writeString(shareText)
      writeString(emailSubject)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<InviteShareBundle> = object : Parcelable.Creator<InviteShareBundle> {
         override fun createFromParcel(source: Parcel): InviteShareBundle = InviteShareBundle(source)
         override fun newArray(size: Int): Array<InviteShareBundle?> = arrayOfNulls(size)
      }
   }

}
