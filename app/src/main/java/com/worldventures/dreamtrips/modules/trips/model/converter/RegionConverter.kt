package com.worldventures.dreamtrips.modules.trips.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.trip.model.TripRegion
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionModel

import io.techery.mappery.MapperyContext

class RegionConverter : Converter<TripRegion, RegionModel> {

   override fun sourceClass() = TripRegion::class.java

   override fun targetClass() = RegionModel::class.java

   override fun convert(context: MapperyContext, source: TripRegion) = RegionModel(source.id(), source.name())
}
