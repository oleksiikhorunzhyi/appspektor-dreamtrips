package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.TripImage;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class TripImageConverter implements Converter<TripImage, com.worldventures.dreamtrips.modules.tripsimages.model.TripImage> {
   @Override
   public Class<TripImage> sourceClass() {
      return TripImage.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.modules.tripsimages.model.TripImage> targetClass() {
      return com.worldventures.dreamtrips.modules.tripsimages.model.TripImage.class;
   }

   @Override
   public com.worldventures.dreamtrips.modules.tripsimages.model.TripImage convert(MapperyContext mapperyContext, TripImage sourceTripImage) {
      com.worldventures.dreamtrips.modules.tripsimages.model.TripImage tripImage = new com.worldventures.dreamtrips.modules.tripsimages.model.TripImage();
      tripImage.setId(sourceTripImage.id());
      tripImage.setUrl(sourceTripImage.url());
      tripImage.setOriginUrl(sourceTripImage.originUrl());
      tripImage.setDescription(sourceTripImage.description());
      tripImage.setType(sourceTripImage.type());
      return tripImage;
   }
}
