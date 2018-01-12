package com.worldventures.wallet.ui.settings.help.video.model

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.core.modules.video.model.Video
import com.worldventures.wallet.ui.common.adapter.BaseViewModel
import com.worldventures.wallet.ui.settings.help.video.holder.VideoTypeFactory

data class WalletVideoModel(val video: Video) : BaseViewModel<VideoTypeFactory>(), Parcelable {

   init {
      modelId = video.videoName
   }

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeString(video.imageUrl)
      dest.writeString(video.videoUrl)
      dest.writeString(video.videoName)
      dest.writeString(video.category)
      dest.writeString(video.duration)
      dest.writeString(video.language)
   }

   constructor(`in`: Parcel) : this(
         Video(`in`.readString(), `in`.readString(), `in`.readString(), `in`.readString(), `in`.readString(), `in`.readString())
   )

   override fun type(typeFactory: VideoTypeFactory): Int = typeFactory.type(this)

   companion object CREATOR : Parcelable.Creator<WalletVideoModel> {

      override fun createFromParcel(source: Parcel) = WalletVideoModel(source)

      override fun newArray(size: Int): Array<WalletVideoModel?> = arrayOfNulls(size)
   }
}
