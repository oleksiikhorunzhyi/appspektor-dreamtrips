package com.worldventures.dreamtrips.modules.trips.model.converter

import com.worldventures.dreamtrips.api.trip.model.TripWithDetails
import com.worldventures.dreamtrips.modules.trips.model.ContentItem

import io.techery.mappery.MapperyContext

class TripWithDetailsToTripConverter : ApiTripToTripConverter<TripWithDetails>() {

   override fun sourceClass() = TripWithDetails::class.java

   override fun convert(context: MapperyContext, source: TripWithDetails) = convertTrip(context, source)
         .apply { content = context.convert(source.contentItems(), ContentItem::class.java) }
}
