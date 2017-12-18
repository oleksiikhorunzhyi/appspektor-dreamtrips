package com.worldventures.dreamtrips.social.ui.membership.bundle

import android.os.Parcel
import android.os.Parcelable

import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate
import com.worldventures.dreamtrips.social.domain.entity.InviteType

class TemplateBundle(val inviteTemplate: InviteTemplate, val email: String, val name: String, val inviteType: InviteType) : Parcelable {

   constructor(source: Parcel) : this(
         source.readParcelable<InviteTemplate>(InviteTemplate::class.java.classLoader),
         source.readString(),
         source.readString(),
         InviteType.values()[source.readInt()]
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeParcelable(inviteTemplate, 0)
      writeString(email)
      writeString(name)
      writeInt(inviteType.ordinal)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<TemplateBundle> = object : Parcelable.Creator<TemplateBundle> {
         override fun createFromParcel(source: Parcel) = TemplateBundle(source)
         override fun newArray(size: Int): Array<TemplateBundle?> = arrayOfNulls(size)
      }
   }
}
