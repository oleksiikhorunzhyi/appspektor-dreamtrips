package com.worldventures.dreamtrips.social.ui.tripsimages.model

import android.os.Parcel
import android.os.Parcelable
import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.worldventures.core.model.ImagePathHolder
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.modules.trips.model.Location
import com.worldventures.dreamtrips.social.ui.feed.model.BaseFeedEntity
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag
import java.util.Date

@DefaultSerializer(CompatibleFieldSerializer::class)
class Photo() : BaseFeedEntity(), ImagePathHolder, Parcelable {

   var title: String? = null
   var shotAt: Date? = null
   /*
    * Can be null if we get photo as attachment from feed
    * There are complications to add createdAt in feed on server
   */
   // Workaround property as backing field to createdAt, refactor after we migrate BaseFeedEntity to Kotlin
   private var photoCreatedAt: Date? = null
   var location: Location? = null
   var tags: MutableList<String> = mutableListOf()
   var url: String = ""
   var photoTags: MutableList<PhotoTag> = mutableListOf()
   var photoTagsCount: Int = 0
   var width: Int = 0
   var height: Int = 0

   constructor(uid: String) : this() {
      this.uid = uid
   }

   override fun place(): String? {
      return if (location != null) location?.let { it.name } else null
   }

   override fun getOriginalText(): String? = title

   override fun getImagePath(): String? = url

   override fun getCreatedAt(): Date? = photoCreatedAt

   fun setCreatedAt(value: Date?) {
      this.photoCreatedAt = value
   }

   constructor(source: Parcel) : this() {
      this.uid = source.readString()
      this.owner = source.readParcelable(User::class.java.classLoader)
      this.commentsCount = source.readInt()
      this.comments = source.createTypedArrayList(Comment.CREATOR)
      this.liked = source.readInt() == 1
      this.likesCount = source.readInt()
      this.language = source.readString()
      this.firstLikerName = source.readString()
      this.translation = source.readString()
      this.isTranslated = source.readInt() == 1

      this.title = source.readString()
      this.shotAt = source.readSerializable() as Date?
      this.photoCreatedAt = source.readSerializable() as Date?
      this.location = source.readSerializable() as Location?
      this.tags = source.createStringArrayList()
      this.url = source.readString()
      this.photoTags = source.createTypedArrayList(PhotoTag.CREATOR)
      this.photoTagsCount = source.readInt()
      this.width = source.readInt()
      this.height = source.readInt()
   }

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(uid)
      writeParcelable(owner, 0)
      writeInt(commentsCount)
      writeTypedList(comments)
      writeInt(if (liked) 1 else 0)
      writeInt(likesCount)
      writeString(language)
      writeString(firstLikerName)
      writeString(translation)
      writeInt(if (isTranslated) 1 else 0)

      writeString(title)
      writeSerializable(shotAt)
      writeSerializable(photoCreatedAt)
      writeSerializable(location)
      writeStringList(tags)
      writeString(url)
      writeTypedList(photoTags)
      writeInt(photoTagsCount)
      writeInt(width)
      writeInt(height)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<Photo> = object : Parcelable.Creator<Photo> {
         override fun createFromParcel(source: Parcel): Photo = Photo(source)
         override fun newArray(size: Int): Array<Photo?> = arrayOfNulls(size)
      }
   }
}
