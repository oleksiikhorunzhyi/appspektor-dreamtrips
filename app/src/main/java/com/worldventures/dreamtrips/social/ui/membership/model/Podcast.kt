package com.worldventures.dreamtrips.social.ui.membership.model

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.worldventures.core.model.CachedModel

import java.util.Date

@DefaultSerializer(CompatibleFieldSerializer::class)
class Podcast {

   var title: String? = null
   var category: String? = null
   var description: String? = null
   var date: Date? = null
   var size: Long = 0
   var duration: Long = 0
   var imageUrl: String? = null
   var fileUrl: String? = null
   var speaker: String? = null

   @Transient
   var cacheEntity: CachedModel? = null
      get() {
         if (field == null) {
            cacheEntity = CachedModel(fileUrl, getUid(), title)
            field!!.entityClass = Podcast::class.java
         }
         return field
      }

   fun getUid() = fileUrl

   override fun toString() =
         "Podcast{" +
               "title='" + title + '\'' +
               ", category='" + category + '\'' +
               ", description='" + description + '\'' +
               ", date=" + date +
               ", size=" + size +
               ", duration=" + duration +
               ", imageUrl='" + imageUrl + '\'' +
               ", fileUrl='" + fileUrl + '\'' +
               ", speaker=" + speaker +
               "}"
}
