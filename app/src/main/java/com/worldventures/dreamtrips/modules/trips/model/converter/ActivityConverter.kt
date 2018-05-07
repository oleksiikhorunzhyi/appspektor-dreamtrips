package com.worldventures.dreamtrips.modules.trips.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.trip.model.TripActivity
import com.worldventures.dreamtrips.modules.trips.model.filter.ActivityModel

import io.techery.mappery.MapperyContext

class ActivityConverter : Converter<TripActivity, ActivityModel> {
   override fun sourceClass() = TripActivity::class.java

   override fun targetClass() = ActivityModel::class.java

   override fun convert(context: MapperyContext, source: TripActivity) = ActivityModel(source.id(), source.parentId(), source.name())
}
