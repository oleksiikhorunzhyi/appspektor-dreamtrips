package com.worldventures.wallet.ui.settings.help.video.impl

import android.os.Parcel
import android.os.Parcelable
import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale

data class HelpVideoLocale(val videoLocale: VideoLocale?, val videoLanguage: VideoLanguage) : Parcelable {

   constructor(source: Parcel) : this(
         source.readSerializable() as VideoLocale,
         source.readSerializable() as VideoLanguage
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeSerializable(videoLocale)
      writeSerializable(videoLanguage)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<HelpVideoLocale> = object : Parcelable.Creator<HelpVideoLocale> {
         override fun createFromParcel(source: Parcel) = HelpVideoLocale(source)
         override fun newArray(size: Int): Array<HelpVideoLocale?> = arrayOfNulls(size)
      }
   }
}
