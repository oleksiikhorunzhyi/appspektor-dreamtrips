package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.TripContent;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;

import io.techery.mappery.MapperyContext;

public class ContentItemConverter implements Converter<TripContent, ContentItem> {

   @Override
   public Class<TripContent> sourceClass() {
      return TripContent.class;
   }

   @Override
   public Class<ContentItem> targetClass() {
      return ContentItem.class;
   }

   @Override
   public ContentItem convert(MapperyContext mapperyContext, TripContent tripContent) {
      ContentItem contentItem = new ContentItem();
      contentItem.setName(tripContent.name());
      contentItem.setDescription(tripContent.description());
      contentItem.setLanguage(tripContent.language());
      contentItem.setTags(tripContent.tags());
      return contentItem;
   }
}
