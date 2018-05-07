package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable

class PhotoMediaEntity(item: Photo) : BaseMediaEntity<Photo>(item, MediaEntityType.PHOTO), Parcelable {

   constructor(parcel: Parcel) : this(parcel.readParcelable<Photo>(Photo::class.java.classLoader))

   override fun describeContents(): Int = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = dest.writeParcelable(item, flags)

   companion object CREATOR : Parcelable.Creator<PhotoMediaEntity> {
      override fun createFromParcel(parcel: Parcel) = PhotoMediaEntity(parcel)
      override fun newArray(size: Int): Array<PhotoMediaEntity?> = arrayOfNulls(size)
   }
}
