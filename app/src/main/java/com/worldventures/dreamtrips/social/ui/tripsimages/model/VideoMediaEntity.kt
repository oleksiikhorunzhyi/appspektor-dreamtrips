package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video

class VideoMediaEntity(val video: Video) : BaseMediaEntity<Video>(video, MediaEntityType.VIDEO), Parcelable {

   constructor(parcel: Parcel) : this(parcel.readParcelable<Video>(Video::class.java.classLoader))

   override fun describeContents(): Int = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = dest.writeParcelable(video, flags)

   companion object CREATOR : Parcelable.Creator<VideoMediaEntity> {
      override fun createFromParcel(parcel: Parcel) = VideoMediaEntity(parcel)
      override fun newArray(size: Int): Array<VideoMediaEntity?> = arrayOfNulls(size)
   }
}
