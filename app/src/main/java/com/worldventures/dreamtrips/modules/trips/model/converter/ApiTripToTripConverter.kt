package com.worldventures.dreamtrips.modules.trips.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.core.model.Location
import com.worldventures.dreamtrips.api.trip.model.TripDates
import com.worldventures.dreamtrips.api.trip.model.TripLocation
import com.worldventures.dreamtrips.api.trip.model.TripPrice
import com.worldventures.dreamtrips.modules.trips.model.Price
import com.worldventures.dreamtrips.modules.trips.model.Schedule
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment
import io.techery.mappery.MapperyContext
import com.worldventures.dreamtrips.api.trip.model.Trip as ApiTrip

abstract class ApiTripToTripConverter<T : ApiTrip> : Converter<T, TripModel> {

   override fun targetClass() = TripModel::class.java

   protected fun convertTrip(mapperyContext: MapperyContext, trip: ApiTrip) =
         TripModel().apply {
            uid = trip.uid()
            isLiked = trip.liked()
            likesCount = trip.likes()
            trip.comments()?.apply {
               comments = mapperyContext.convert(this, Comment::class.java)
            }
            commentsCount = trip.commentsCount()
            tripId = trip.tripId()
            name = trip.name()
            description = trip.description()
            isFeatured = trip.featured()
            isPlatinum = trip.platinum()
            price = price(trip.price())
            rewardsLimit = trip.rewardsLimit()
            isSoldOut = trip.soldOut()
            location = location(trip.location())
            duration = trip.duration()
            hasMultipleDates = trip.hasMultipleDates()
            availabilityDates = dates(trip.dates())
            isInBucketList = trip.inBucketList()
            thumbnailUrl = getImageUrl(trip, THUMB)
            imageUrls = getFilteredImages(trip)
         }

   private fun dates(tripDates: TripDates) = Schedule(tripDates.startOn(), tripDates.endOn())

   private fun location(tripLocation: TripLocation) = Location(tripLocation.lat(), tripLocation.lng())
         .apply { name = tripLocation.name() }

   private fun price(tripPrice: TripPrice) = Price(tripPrice.amount(), tripPrice.currency())

   private fun getImageUrl(trip: ApiTrip, type: String): String {
      if (trip.images() != null) {
         val tripImage = trip.images().firstOrNull { it.type() == type }
         if (tripImage != null) return tripImage.url()
      }
      return ""
   }

   private fun getFilteredImages(trip: ApiTrip): List<String> {
      val retinaImageUrls = getFilteredImagesByTag(trip, RETINA)
      return if (retinaImageUrls.isEmpty()) getFilteredImagesByTag(trip, NORMAL)
      else retinaImageUrls
   }

   private fun getFilteredImagesByTag(trip: ApiTrip, tag: String) = trip.images().filter { tag == it.type() }.map { it.url() }

   companion object {
      private val THUMB = "THUMB"
      private val RETINA = "RETINA"
      private val NORMAL = "NORMAL"
   }
}
