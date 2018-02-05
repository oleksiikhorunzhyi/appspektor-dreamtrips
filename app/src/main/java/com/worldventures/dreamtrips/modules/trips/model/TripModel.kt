package com.worldventures.dreamtrips.modules.trips.model

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.worldventures.core.model.Location
import com.worldventures.dreamtrips.social.ui.feed.model.BaseFeedEntity
import java.io.Serializable

@DefaultSerializer(CompatibleFieldSerializer::class)
class TripModel : BaseFeedEntity(), Serializable {

   var tripId = ""
   var name = ""
   var description = ""
   var thumbnailUrl = ""
   var imageUrls: List<String> = emptyList()
   var duration = 0
   var hasMultipleDates = false
   var isSoldOut = false
   var isFeatured = false
   var isPlatinum = false
   var isInBucketList = false
   var rewardsLimit = 0L
   var price: Price? = null
   var location: Location? = null
   var availabilityDates: Schedule? = null
   var content: List<ContentItem>? = null

   override fun place() = location?.name

   fun getThumb(size: Int) = getThumb(size, size)

   fun getThumb(width: Int, height: Int) = thumbnailUrl + String.format(PATTERN, width, height)

   companion object {
      val PATTERN = "?width=%d&height=%d"
      val serialVersionUID = 123L
   }
}
