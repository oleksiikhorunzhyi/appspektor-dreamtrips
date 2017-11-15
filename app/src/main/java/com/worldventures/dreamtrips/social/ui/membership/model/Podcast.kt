package com.worldventures.dreamtrips.social.ui.membership.model

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.worldventures.core.model.CachedModel

import java.util.Date

@DefaultSerializer(CompatibleFieldSerializer::class)
data class Podcast(var title: String? = null,
                   var category: String? = null,
                   var description: String? = null,
                   var date: Date? = null,
                   var size: Long = 0,
                   var duration: Long = 0,
                   var imageUrl: String? = null,
                   var fileUrl: String? = null,
                   var speaker: String? = null,
                   var cachedModel: CachedModel? = null) {

   override fun equals(other: Any?) = (other !is Podcast) || this.fileUrl.equals(other.fileUrl)

}
