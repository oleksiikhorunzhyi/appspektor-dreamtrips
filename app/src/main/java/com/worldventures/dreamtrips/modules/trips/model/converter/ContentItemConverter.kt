package com.worldventures.dreamtrips.modules.trips.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.trip.model.TripContent
import com.worldventures.dreamtrips.modules.trips.model.ContentItem

import io.techery.mappery.MapperyContext

class ContentItemConverter : Converter<TripContent, ContentItem> {

   override fun sourceClass() = TripContent::class.java

   override fun targetClass() = ContentItem::class.java

   override fun convert(context: MapperyContext, source: TripContent) = ContentItem().apply {
      description = source.description()
      language = source.language()
      tags = source.tags()
      name = source.name()
   }
}
