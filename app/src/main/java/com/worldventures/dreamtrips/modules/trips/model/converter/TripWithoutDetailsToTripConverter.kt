package com.worldventures.dreamtrips.modules.trips.model.converter

import com.worldventures.dreamtrips.api.trip.model.TripWithoutDetails
import io.techery.mappery.MapperyContext

class TripWithoutDetailsToTripConverter : ApiTripToTripConverter<TripWithoutDetails>() {

   override fun sourceClass() = TripWithoutDetails::class.java

   override fun convert(context: MapperyContext, source: TripWithoutDetails) = convertTrip(context, source)
}
