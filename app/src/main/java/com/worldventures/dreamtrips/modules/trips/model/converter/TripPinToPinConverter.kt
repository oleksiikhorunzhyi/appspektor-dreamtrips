package com.worldventures.dreamtrips.modules.trips.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.trip.model.TripPinWrapper
import com.worldventures.dreamtrips.modules.trips.model.map.Coordinates
import com.worldventures.dreamtrips.modules.trips.model.map.Pin
import io.techery.mappery.MapperyContext
import java.util.ArrayList

class TripPinToPinConverter : Converter<TripPinWrapper, Pin> {

   override fun sourceClass() = TripPinWrapper::class.java

   override fun targetClass() = Pin::class.java

   override fun convert(context: MapperyContext, source: TripPinWrapper) =
         source.item().let {
            Pin(Coordinates(it.coordinates().lat(), it.coordinates().lng()), ArrayList(it.tripsUids()))
         }
}
